package org.heterojni.sparkjni.utils;

public class BuildMode {
    protected boolean doJavah;
    protected boolean doClean = true;
    protected boolean doBuild = true;
    protected boolean doGenerateMakefile = true;
    protected boolean doForceOverwriteKernelFiles = true;

    public enum BuildModes {
            FULL_GENERATE_AND_BUILD,
            JAVAH_MAKEFILE_AND_BUILD,
            JUST_BUILD,
            ASSUME_EVERYTHING_IS_THERE
    }

    public BuildMode(BuildModes buildMode) {
        switch (buildMode) {
            case FULL_GENERATE_AND_BUILD:
                doBuild = true;
                doGenerateMakefile = true;
                doForceOverwriteKernelFiles = true;
                doJavah = true;
                break;
            case JUST_BUILD:
                doBuild = true;
                doGenerateMakefile = false;
                doForceOverwriteKernelFiles = false;
                doJavah = false;
                break;
            case JAVAH_MAKEFILE_AND_BUILD:
                doBuild = true;
                doGenerateMakefile = true;
                doForceOverwriteKernelFiles = false;
                doJavah = true;
            case ASSUME_EVERYTHING_IS_THERE:
                break;
            default:
                // throw smth
        }
    }
}