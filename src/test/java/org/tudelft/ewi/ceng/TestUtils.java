package org.tudelft.ewi.ceng;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * Created by root on 9/21/16.
 */
public class TestUtils {
    public static final String CLUSTER_CONF_LOCAL_4 = "local[4]";
    public static final String JAVA_HOME_ENV = "JAVA_HOME";
    public String defaultTestFolder = "resources/JNI_UTILS_TEST";
    public String fullPath;
    public File testDir;
    public static String jdkPath;
    public static String appName;
    private static JavaSparkContext jscSingleton;

    public TestUtils(String appName){
        jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64";
        this.appName = appName;
    }

    public void initTestDir(){
        testDir = new File(defaultTestFolder);
        if(testDir.exists())
            fail(String.format("Directory %s already exists", defaultTestFolder));
        else
            testDir.mkdir();

        File resourcesDir = new File("resources/");
        fullPath = resourcesDir.getAbsolutePath();
    }

    public void cleanTestDir(){
        if(testDir != null && testDir.exists())
            testDir.delete();
    }

    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster(CLUSTER_CONF_LOCAL_4);
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }
}
