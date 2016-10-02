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
package org.tudelft.ceng.sparkjni.utils;

import org.apache.spark.api.java.JavaSparkContext;
import org.tudelft.ceng.sparkjni.exceptions.HardSparkJniException;
import org.tudelft.ceng.sparkjni.exceptions.Messages;
import org.tudelft.ceng.sparkjni.javaLink.JniHeaderHandler;
import org.tudelft.ceng.sparkjni.javaLink.JniLinkHandler;

import java.io.*;

/**
 * Top level class with static functionality that handles the SparkJNI execution.
 */
public class SparkJni {
    public static final String LIB_PATH_STR = "%s/%s.so";
    private static boolean doClean = true;
    private static boolean doBuild = true;
    private static boolean doGenerateMakefile = true;
    private static boolean doWriteKernelFiles = true;

    private static JniLinkHandler jniLinkHandler;
    private static MetadataHandler metadataHandler;
    private static JavaSparkContext javaSparkContext;

    private static long start = 0L;
    private static long genTime = 0L;
    private static long buildTime = 0L;
    private static long libLoadTime = 0L;
    private static long javahTime = 0L;

    public static long getJavahTime() {
        return javahTime;
    }
    public static long getLibLoadTime() {
        return libLoadTime;
    }
    public static long getGenTime() {
        return genTime;
    }
    public static long getBuildTime() {
        return buildTime;
    }

    static{
        metadataHandler = MetadataHandler.getHandler();
        jniLinkHandler = JniLinkHandler.getJniLinkHandlerSingleton();
    }

    private SparkJni() {}

    /**
     * Function which triggers the SparkJNI processes.
     * @param appName The target app name.
     * @param sparkContext The Spark Context. Leave null if running without Spark.
     */
    public static void deploy(String appName, JavaSparkContext sparkContext) {
        initVars(appName, sparkContext);
        processCppContent();
        loadNativeLib();
    }

    private static void initVars(String appName, JavaSparkContext sparkContext){
        start = System.currentTimeMillis();
        setAppName(appName);
        setSparkContext(sparkContext);
    }

    private static void loadNativeLib(){
        String libraryFullPath = String.format(LIB_PATH_STR, metadataHandler.getNativePath(), metadataHandler.getAppName());
        if (javaSparkContext != null)
            javaSparkContext.addFile(libraryFullPath);
        else
            System.load(libraryFullPath);
        libLoadTime = System.currentTimeMillis() - start;
    }

    private static void processCppContent(){
        checkNativePath();
        cleanHeaderFiles();
        if(jniLinkHandler != null)
            jniLinkHandler.deployLink();
        else
            throw new RuntimeException("NOT SET");

        long startJavah = System.currentTimeMillis();
        jniLinkHandler.javah(JniUtils.getClasspath());
        collectNativeFunctionPrototypes();
        javahTime = System.currentTimeMillis() - startJavah;

        // TO-DO: Populate kernel files.
        generateAndCheckMakefile();
        generateKernelFiles();
        build();
    }

    private static void cleanHeaderFiles(){
        JniDirAccessor dirAccessor = new JniDirAccessor(metadataHandler.getNativePath());
        for(JniHeaderHandler header: dirAccessor.getJniHeaderHandlers())
            if(header.removeHeaderFile())
                dirAccessor.getJniHeaderHandlers().remove(header);
    }

    static void generateKernelFiles(){
        if (doWriteKernelFiles)
            if (!jniLinkHandler.getKernelFile().writeKernelFile())
                throw new HardSparkJniException(Messages.ERR_KERNEL_FILE_GENERATION_FAILED);
    }

    static void generateAndCheckMakefile(){
        if (doGenerateMakefile)
            if (!generateMakefile()) {
                System.err.println(Messages.MAKEFILE_GENERATION_FAILED_ERROR);
                System.exit(3);
            }
    }

    static void build(){
        String nativePath = metadataHandler.getNativePath();
        genTime = System.currentTimeMillis() - start - javahTime;
        start = System.currentTimeMillis();
        if(doBuild) {
            runProcess(String.format(CppSyntax.EXEC_MAKE_CLEAN, nativePath));
            runProcess(String.format(CppSyntax.EXEC_MAKE, nativePath));
        }
        buildTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
    }

    private static void checkNativePath(){
        if (metadataHandler.getNativePath() == null) {
            System.err.println(Messages.NATIVE_PATH_NOT_SET);
            System.exit(1);
        }
        File nativePathDir = new File(metadataHandler.getNativePath());
        if (!nativePathDir.exists() || !nativePathDir.isDirectory()) {
            System.err.println(Messages.NATIVE_PATH_ERROR);
            System.exit(2);
        }
    }

    /**
     * Enable automatic makefile generation at the end of the deployment stage.
     * @param doGenerateMakefile
     */
    public static void setDoGenerateMakefile(boolean doGenerateMakefile) {
        SparkJni.doGenerateMakefile = doGenerateMakefile;
    }

    /**
     * Enable building the shared native library.
     * @param doBuild
     */
    public static void setDoBuild(boolean doBuild) {
        SparkJni.doBuild = doBuild;
    }

    /**
     * Set the user defines pragma for the build stage flags.
     * @param userDefines
     */
    public static void setUserDefines(String userDefines) {
        metadataHandler.setUserDefines(userDefines);
    }

    /**
     * Set the personalized user directories.
     * @param userLibraryDirs
     */
    public static void setUserLibraryDirs(String userLibraryDirs) {
        metadataHandler.setUserLibraryDirs(userLibraryDirs);
    }

    public static void setSparkContext(JavaSparkContext javaSparkContext) {
        SparkJni.javaSparkContext = javaSparkContext;
    }

    /**
     * Trigger the writing of the template file.
     * @param doWriteKernelFiles
     */
    public static void setDoWriteKernelFiles(boolean doWriteKernelFiles) {
        SparkJni.doWriteKernelFiles = doWriteKernelFiles;
    }

    /**
     * Set the personalized user include directories.
     * @param userIncludeDirs
     */
    public static void setUserIncludeDirs(String userIncludeDirs) {
        metadataHandler.setUserIncludeDirs(userIncludeDirs);
    }

    public static void setUserLibraries(String userLibraries) {
        metadataHandler.setUserLibraries(userLibraries);
    }

    public static void setJdkPath(String jdkPath) {
        metadataHandler.setJdkPath(jdkPath);
    }

    public static void setNativePath(String nativePath) {
        metadataHandler.setNativePath(nativePath);
    }

    public static void setAppName(String appName) {
        metadataHandler.setAppName(appName);
    }

    /**
     * Register the user-defined jni function.
     * @param jniFunctionClass
     */
    public static void registerJniFunction(Class jniFunctionClass) {
        jniLinkHandler.registerJniFunction(jniFunctionClass);
    }

    /**
     * Register the user-defined JavaBean container.
     * @param beanClass
     */
    public static void registerContainer(Class beanClass) {
        jniLinkHandler.registerBean(beanClass);
    }

    private static boolean generateMakefile() {
        String newMakefileContent = String.format(CppSyntax.NEW_MAKEFILE_SECTION,
                metadataHandler.getAppName(), metadataHandler.getJdkPath(), metadataHandler.getUserIncludeDirs(),
                metadataHandler.getUserLibraryDirs(), metadataHandler.getUserLibraries(),
                metadataHandler.getUserStaticLibraries(), metadataHandler.getUserDefines());

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(metadataHandler.getNativePath() + "/Makefile");
            writer.write(newMakefileContent);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (writer != null)
                writer.close();
        }

        return true;
    }

    public static boolean collectNativeFunctionPrototypes() {
        File nativeLibDir = new File(metadataHandler.getNativePath());

        if (nativeLibDir.exists() && nativeLibDir.isDirectory()) {
            File[] headerFiles = nativeLibDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.endsWith(".h");
                }
            });
            for (File headerFile : headerFiles) {
                jniLinkHandler.getJniHeaderFiles().add(headerFile.getName());
                String[] splittedFileName = headerFile.getName().split("_");
                if (splittedFileName.length == 1)
                    continue;

                String className = splittedFileName[splittedFileName.length - 1];
                className = className.substring(0, className.length() - 2);

                try (BufferedReader br = new BufferedReader(new FileReader(headerFile))) {
                    for (String line; (line = br.readLine()) != null; ) {
                        if (line.startsWith("JNIEXPORT")) {
                            String[] splitted = line.split("_");
                            String methodName = splitted[splitted.length - 1];
                            jniLinkHandler.registerNativePrototype(line, methodName);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return true;
        } else
            return false;
    }

    public static void runProcess(String proc) {
        try {
            Process process = Runtime.getRuntime().exec(proc);
            InputStream errors = process.getErrorStream(),
                    input = process.getInputStream();
            InputStreamReader errorStreamReader = new InputStreamReader(errors),
                    inputStreamReader = new InputStreamReader(input);
            BufferedReader errorBufferedReader = new BufferedReader(errorStreamReader),
                    inputBufferedReader = new BufferedReader(inputStreamReader);
            String line = null;

            while ((line = errorBufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = inputBufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            if (process.waitFor() != 0) {
                throw new HardSparkJniException(String.format("[ERROR] %s:\n\t%s",
                        Messages.ERR_CPP_BUILD_FAILED, proc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void setUserStaticLibraries(String userStaticLibraries) {
        metadataHandler.setUserStaticLibraries(userStaticLibraries);
    }

    public static boolean isDoClean() {
        return doClean;
    }

    public static JniLinkHandler getJniHandler() {
        return jniLinkHandler;
    }

    public static long getStart() {
        return start;
    }

    /**
     * @TODO Complete implementation of the reset feature for persistent environments, where the JVM is not closed
     * between application launches. Or transform to singleton.
     */
    public static void reset() {
        getJniHandler().reset();
    }
}
