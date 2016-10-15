package org.heterojni;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.SparkJni;
import org.heterojni.sparkjni.utils.SparkJniSingletonBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 9/21/16.
 */
public class TestUtils {
    public static final String CLUSTER_CONF_LOCAL_4 = "local[4]";
    public static final String JAVA_HOME_ENV = "JAVA_HOME";
    public String defaultTestFolder = "resources/%s";
    public File testDir;
    public String fullPath;
    public String jdkPath;
    public String appName;
    private static JavaSparkContext jscSingleton = null;

    public TestUtils(Class callerClass){
        jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64";
        appName = callerClass.getSimpleName();
        defaultTestFolder = String.format(defaultTestFolder, appName+"_TEST");
    }

    public void initTestDir(){
        testDir = new File(defaultTestFolder);
        if(testDir.exists())
            cleanTestDir();
        testDir.mkdir();

        fullPath = testDir.getAbsolutePath();
    }

    public void cleanTestDir(){
        try {
            FileUtils.deleteDirectory(testDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JavaSparkContext getSparkContext(){
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

    public SparkJni getSparkJni(){
        SparkJni.reset();
        SparkJni sparkJni =  new SparkJniSingletonBuilder()
                .appName(appName)
                .jdkPath(jdkPath)
                .nativePath(fullPath)
                .build();
        return sparkJni;
    }
}
