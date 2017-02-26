package vectorOps;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import sparkjni.utils.CppSyntax;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GeneratorVectorOpsMain {
    private static JavaSparkContext jscSingleton;
    private static String appName = "vectorOps";
    private static boolean debug = true;

    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster("local[4]");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    public static ArrayList<VectorBean> generateVectors(int noVectors, int vectorSize){
        ArrayList<VectorBean> vectors = new ArrayList<VectorBean>();
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
        String nativePath = Paths.get("src/test/resources/vectorOps/src/main/resources/vectorOps").toAbsolutePath().toString();
        String relativeLibPath = String.format(CppSyntax.NATIVE_LIB_PATH, nativePath, appName);
        String absLibPath = new File(relativeLibPath).toPath().toAbsolutePath().toString();
        JavaRDD<VectorBean> vectorsRdd = getSparkContext().parallelize(generateVectors(2, 4));
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(absLibPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(absLibPath, "reduceVectorAdd"));
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