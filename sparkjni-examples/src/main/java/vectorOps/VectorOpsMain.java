package vectorOps;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ArrayList;

import static sparkjni.utils.DeployMode.DeployModes.JUST_BUILD;

public class VectorOpsMain {
    private static JavaSparkContext jscSingleton;
    private static String nativePath = null;
    private static String appName = "vectorOps";
    private static final boolean debug = true;
    private static DeployMode deployMode = new DeployMode(JUST_BUILD);

    private static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster("local[4]");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    private static void initSparkJNI(){
        nativePath = Paths.get("sparkjni-examples/src/main/cpp/examples/vectorOps").normalize().toAbsolutePath().toString();
        appName = "vectorOps";

        String sparkjniClasspath = FileSystems.getDefault().getPath("core/target/classes").toAbsolutePath().normalize().toString();
        String examplesClasspath = FileSystems.getDefault().getPath("sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();

        SparkJni sparkJni = new SparkJniSingletonBuilder()
                .nativePath(nativePath)
                .appName(appName)
                .build();

        sparkJni.setDeployMode(new DeployMode(JUST_BUILD))
                .addToClasspath(sparkjniClasspath, examplesClasspath);

        sparkJni.registerContainer(VectorBean.class)
                .registerJniFunction(VectorMulJni.class)
                .registerJniFunction(VectorAddJni.class);
        sparkJni.deploy();
    }

    private static ArrayList<VectorBean> generateVectors(int noVectors, int vectorSize){
        ArrayList<VectorBean> vectors = new ArrayList<>();
        for(int i = 0; i < noVectors; i++){
            int[] data = new int[vectorSize];
            if(debug)
                System.out.println(String.format("Vector %d:", i));
            for(int idx = 0; idx < vectorSize; idx++) {
                data[idx] = (int) (Math.random() * 1000);
                if(debug)
                    System.out.println(String.format("idx %d: %d", idx, data[idx]));
            }
            vectors.add(new VectorBean(data));
        }
        return vectors;
    }

    public static void main(String[] args){
        initSparkJNI();
        String libPath = String.format(CppSyntax.NATIVE_LIB_PATH, nativePath, appName);
        JavaRDD<VectorBean> vectorsRdd = getSparkContext().parallelize(generateVectors(2, 4));
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
        debugRes(results);
    }

    private static void debugRes(VectorBean vector){
        if(debug) {
            System.out.println("Result:");
            for (int i = 0; i < vector.data.length; i++)
                System.out.println(vector.data[i]);
        }
    }
}