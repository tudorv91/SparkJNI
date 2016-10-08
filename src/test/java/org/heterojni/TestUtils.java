package org.heterojni;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.heterojni.sparkjni.utils.JniUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Created by root on 9/21/16.
 */
public class TestUtils {
    public static final String CLUSTER_CONF_LOCAL_4 = "local[4]";
    public static final String JAVA_HOME_ENV = "JAVA_HOME";
    public String defaultTestFolder = "resources/%s";
    public String fullPath;
    public File testDir;
    public static String jdkPath;
    public static String appName;
    private static JavaSparkContext jscSingleton = null;

    public TestUtils(Class callerClass){
        jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64";
        appName = callerClass.getSimpleName();
        defaultTestFolder = String.format(defaultTestFolder, appName+"_TEST");
    }

    public void initTestDir(){
        testDir = new File(defaultTestFolder);
        if(testDir.exists())
            testDir.delete();
        testDir.mkdir();

        File resourcesDir = new File(defaultTestFolder);
        fullPath = resourcesDir.getAbsolutePath();
    }

    public void cleanTestDir(){
        try {
            FileUtils.deleteDirectory(testDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster(CLUSTER_CONF_LOCAL_4);
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    public static String getAbsolutePathFor(String relativePath){
        return JniUtils.class.getClassLoader().getResource(relativePath).toString();
    }
}
