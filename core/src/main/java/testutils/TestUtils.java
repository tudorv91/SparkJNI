package testutils;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import sparkjni.utils.JniUtils;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 9/21/16.
 */
public class TestUtils {
    public static final String CLUSTER_CONF_LOCAL_4 = "local[4]";
    public String defaultTestFolder = "resources/%s";
    public File testDir;
    public String fullPath;
    public String appName;
    private static JavaSparkContext jscSingleton = null;

    public TestUtils(Class callerClass){
        appName = callerClass.getSimpleName();
        defaultTestFolder = String.format(defaultTestFolder, appName+"_TEST");
        initTestDir();
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

    public SparkJni getSparkJni(String classpath){
        SparkJni.reset();
        System.gc();
        SparkJni sparkJni =  new SparkJniSingletonBuilder()
                .appName(appName)
                .nativePath(fullPath)
                .build();
        sparkJni.setClasspath(classpath);
        return sparkJni;
    }

    public String getLibPath(){
        return JniUtils.generateDefaultLibPath(appName, fullPath);
    }

    public SparkJni getSparkJni(){
        return getSparkJni(JniUtils.getClasspath());
    }
}
