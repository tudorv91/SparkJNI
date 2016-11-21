/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package testutils;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import sparkjni.utils.JniUtils;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.io.File;
import java.io.IOException;

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
        testDir.mkdirs();

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
