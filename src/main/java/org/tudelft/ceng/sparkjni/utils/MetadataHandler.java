package org.tudelft.ceng.sparkjni.utils;

import org.tudelft.ceng.sparkjni.exceptions.HardSparkJniException;
import org.tudelft.ceng.sparkjni.exceptions.Messages;

/**
 * Created by root on 9/24/16.
 */
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
    private MetadataHandler(){}

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
        if(classpath == null || classpath.isEmpty())
            classpath = MetadataHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    public String getJdkPath() {
        if(jdkPath == null || jdkPath.isEmpty())
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
        if(handler == null)
            handler = new MetadataHandler();
        return handler;
    }
}
