/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sparkjni.utils;

import sparkjni.utils.exceptions.HardSparkJniException;
import sparkjni.utils.exceptions.Messages;

import java.io.File;
import java.nio.file.FileSystems;

public class MetadataHandler {
    public static final String JVM_DEFAULT_JAVA = "/usr/lib/jvm/default-java";
    private String appName;
    private String nativePath;
    private String classpath;
    private String jdkPath;
    private String nativeLibPath;
    private String userLibraries = "";
    private String userIncludeDirs = "";
    private String userLibraryDirs = "";
    private String userStaticLibraries = "";
    private String userDefines = "";

    private static MetadataHandler handler = null;

    private MetadataHandler() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getNativePath() {
        return nativePath;
    }

    public void setNativePath(String nativePath) {
        this.nativePath = nativePath;
    }

    public String getClasspath() {
        String sparkJniRoot = resolveSparkJniRoot();
        String coreClasspath = FileSystems.getDefault().getPath(sparkJniRoot+"core/target/classes").toAbsolutePath().normalize().toString();
        String coreTestClasspath = FileSystems.getDefault().getPath(sparkJniRoot+"core/target/test-classes").toAbsolutePath().normalize().toString();
        String examplesClasspath = FileSystems.getDefault().getPath(sparkJniRoot+"sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String benchmarksClasspath = FileSystems.getDefault().getPath(sparkJniRoot+"benchmarks/target/classes").toAbsolutePath().normalize().toString();
        String integrationTestsClasspath = FileSystems.getDefault().getPath(sparkJniRoot+"integration-tests/target/test-classes").toAbsolutePath().normalize().toString();
        String thisJarClasspath = FileSystems.getDefault().getPath("sparkjni-benchmarks.jar").toAbsolutePath().normalize().toString();
        String concatenatedCP = String.format("%s:%s:%s:%s:%s:%s", coreClasspath, coreTestClasspath, examplesClasspath, integrationTestsClasspath, benchmarksClasspath, thisJarClasspath);
        return classpath == null ? concatenatedCP : classpath + ":" + concatenatedCP;
    }

    private String resolveSparkJniRoot() {
        String currentDir = new File(".").getAbsolutePath();
        String[] pathSections = currentDir.split("/");
        int idx = 0;
        for(idx = 0; idx < pathSections.length; idx++){
            if(pathSections[idx].equals("SparkJNI"))
                break;
        }
        String sparkJniRoot = "";
        for(int j = 0; j <= idx; j++)
            sparkJniRoot += (pathSections[j] + "/");
        return sparkJniRoot;
    }

    public String getJdkPath() {
        String envJdkPath = System.getenv().get("JAVA_HOME");
        if(envJdkPath != null && !envJdkPath.isEmpty())
            return envJdkPath;
        else if(new File(JVM_DEFAULT_JAVA).exists())
            return JVM_DEFAULT_JAVA;
        if (jdkPath == null || jdkPath.isEmpty())
            throw new HardSparkJniException(Messages.ERR_PLEASE_DO_SET_THE_JDK_PATH);
        return jdkPath;
    }

    public void setJdkPath(String jdkPath) {
        this.jdkPath = jdkPath;
    }

    public String getUserLibraries() {
        return userLibraries;
    }

    public void setUserLibraries(String userLibraries) {
        this.userLibraries = userLibraries;
    }

    public String getUserIncludeDirs() {
        return userIncludeDirs;
    }

    public void setUserIncludeDirs(String userIncludeDirs) {
        this.userIncludeDirs = userIncludeDirs;
    }

    public String getUserLibraryDirs() {
        return userLibraryDirs;
    }

    public void setUserLibraryDirs(String userLibraryDirs) {
        this.userLibraryDirs = userLibraryDirs;
    }

    public String getUserStaticLibraries() {
        return userStaticLibraries;
    }

    public void setUserStaticLibraries(String userStaticLibraries) {
        this.userStaticLibraries = userStaticLibraries;
    }

    public String getUserDefines() {
        return userDefines;
    }

    public void setUserDefines(String userDefines) {
        this.userDefines = userDefines;
    }

    public static MetadataHandler getHandler() {
        if (handler == null)
            handler = new MetadataHandler();
        return handler;
    }

    protected static void reset() {
        handler = null;
    }

    public void addToClasspath(String cPath) {
        classpath += ":" + cPath;
    }

    public String getNativeLibPath() {
        return nativeLibPath;
    }

    public void setNativeLibPath(String nativeLibPath) {
        this.nativeLibPath = nativeLibPath;
    }
}
