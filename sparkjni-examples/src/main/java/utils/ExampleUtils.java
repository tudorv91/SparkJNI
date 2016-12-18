package utils;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;
import vectorOps.VectorAddJni;
import vectorOps.VectorBean;
import vectorOps.VectorMulJni;

import java.nio.file.FileSystems;
import java.util.ArrayList;

import static sparkjni.utils.DeployMode.DeployModes.JUST_BUILD;

public class ExampleUtils {
    private static JavaSparkContext jscSingleton;

    public static ArrayList<VectorBean> generateVectors(int noVectors, int vectorSize, boolean debug){
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

    public static JavaSparkContext getSparkContext(String appName){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster("local[*]");
            sparkConf.set("spark.driver.maxResultSize", "16g");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    public static void initSparkJNI(String appName, String nativePath){
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
}
