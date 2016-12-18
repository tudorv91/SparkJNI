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

public class MetadataHandler {
    private String appName;
    private String nativePath;
    private String classpath;
    private String jdkPath;
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
        if (classpath == null || classpath.isEmpty())
            classpath = MetadataHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getJdkPath() {
        String envJdkPath = System.getenv().get("JAVA_HOME");
        if(envJdkPath != null && !envJdkPath.isEmpty())
            return envJdkPath;
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
}
