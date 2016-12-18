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
package sparkjni.utils;

public class DeployMode {
    protected boolean doJavah;
    protected boolean doClean = true;
    protected boolean doBuild = true;
    protected boolean doGenerateMakefile = true;
    protected boolean doForceOverwriteKernelFiles = true;

    public enum DeployModes {
            FULL_GENERATE_AND_BUILD,
            JAVAH_MAKEFILE_AND_BUILD,
            JUST_BUILD,
            ASSUME_EVERYTHING_IS_THERE
    }

    public DeployMode(DeployModes buildMode) {
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
                doBuild = false;
                doGenerateMakefile = false;
                doForceOverwriteKernelFiles = false;
                doJavah = false;
                break;
            default:
                // throw smth
        }
    }
}