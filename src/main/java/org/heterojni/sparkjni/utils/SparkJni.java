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
package org.heterojni.sparkjni.utils;

import com.google.common.reflect.ClassPath;
import org.apache.spark.api.java.JavaSparkContext;
import org.heterojni.sparkjni.jniLink.linkHandlers.*;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_class;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_functionClass;
import org.heterojni.sparkjni.utils.exceptions.HardSparkJniException;
import org.heterojni.sparkjni.utils.exceptions.Messages;
import org.heterojni.sparkjni.utils.exceptions.SoftSparkJniException;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.immutables.builder.Builder;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Top level class with static functionality that handles the SparkJNI execution.
 */

public class SparkJni {
    private static JniLinkHandler jniLinkHandler;
    private static MetadataHandler metadataHandler;
    private static JniRootContainer jniRootContainer;
    private static JavaSparkContext javaSparkContext;
    private static SparkJni sparkJniSingleton = null;

    private DeployMode deployMode;
    private DeployTimesLogger deployTimesLogger;

    @Builder.Factory
    public static SparkJni sparkJniSingleton(String appName, String nativePath, String jdkPath) {
        if (sparkJniSingleton == null) {
            sparkJniSingleton = new SparkJni();
            sparkJniSingleton.initVars(appName, nativePath, jdkPath);
        }
        return sparkJniSingleton;
    }

    private SparkJni() {
        // by default, follow the entire deploy process
        deployMode = new DeployMode(DeployMode.DeployModes.FULL_GENERATE_AND_BUILD);
        deployTimesLogger = new DeployTimesLogger();
    }

    private void initVars(String appName, String nativePath, String jdkPath) {
        metadataHandler = MetadataHandler.getHandler();
        setAppName(appName);
        setNativePath(nativePath);
        setJdkPath(jdkPath);
        jniLinkHandler = JniLinkHandler.getJniLinkHandlerSingleton();
    }

    public void deploy() {
        deployTimesLogger.start = System.currentTimeMillis();
        processCppContent();
        loadNativeLib();
    }

    private void loadNativeLib() {
        String libraryFullPath = JniUtils.generateDefaultLibPath(metadataHandler.getAppName(), metadataHandler.getNativePath());
        if (javaSparkContext != null)
            javaSparkContext.addFile(libraryFullPath);
        else
            System.load(libraryFullPath);
        deployTimesLogger.libLoadTime = System.currentTimeMillis() - deployTimesLogger.start;
    }

    private void processCppContent() {
        checkNativePath();
//        try {
//            cleanHeaderFiles();
//        } catch (SoftSparkJniException ex){
//            ex.printStackTrace();
//        }
        if (jniLinkHandler != null)
            jniLinkHandler.deployLink();
        else
            throw new RuntimeException("NOT SET");

        long startJavah = System.currentTimeMillis();
        if (deployMode.doJavah)
            jniLinkHandler.javah(JniUtils.getClasspath());
        collectNativeFunctionPrototypes();
        deployTimesLogger.javahTime = System.currentTimeMillis() - startJavah;

        // TO-DO: Populate kernel files.
        generateAndCheckMakefile();
        generateJniRootContainer();
        generateKernelFiles();
        build();
    }

    private void generateJniRootContainer() {
        jniRootContainer = ImmutableJniRootContainerProvider.builder().build()
                .buildJniRootContainer(metadataHandler.getNativePath(), metadataHandler.getAppName());
    }

    private void cleanHeaderFiles() throws SoftSparkJniException {
        File nativeDir = new File(metadataHandler.getNativePath());
        if (nativeDir.isDirectory()) {
            for (File file : nativeDir.listFiles()) {
                try {
                    if (JniUtils.isJniNativeFunction(file.toPath()))
                        file.delete();
                } catch (IOException ex) {}
            }
        }
    }

    private void generateKernelFiles() {
        if(!deployMode.doForceOverwriteKernelFiles)
            return;
        String defaultKernelWrapperFileName = JniUtils
                .generateDefaultHeaderWrapperFileName(metadataHandler.getAppName(), metadataHandler.getNativePath());
        if (!getKernelFileWrapperHeader().writeKernelWrapperFile())
            throw new HardSparkJniException(Messages.ERR_KERNEL_FILE_GENERATION_FAILED);
        getKernelFileForHeader(defaultKernelWrapperFileName).writeKernelFile();
    }

    private void generateAndCheckMakefile() {
        if (deployMode.doGenerateMakefile)
            if (!generateMakefile()) {
                System.err.println(Messages.MAKEFILE_GENERATION_FAILED_ERROR);
                System.exit(3);
            }
    }

    private void build() {
        String nativePath = metadataHandler.getNativePath();
        deployTimesLogger.genTime = System.currentTimeMillis() - deployTimesLogger.start - deployTimesLogger.javahTime;
        deployTimesLogger.start = System.currentTimeMillis();
        if (deployMode.doBuild) {
            JniUtils.runProcess(String.format(CppSyntax.EXEC_MAKE_CLEAN, nativePath));
            JniUtils.runProcess(String.format(CppSyntax.EXEC_MAKE, nativePath));
        }
        deployTimesLogger.buildTime = System.currentTimeMillis() - deployTimesLogger.start;
    }

    private void checkNativePath() {
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
     * Set the user defines pragma for the build stage flags.
     * @param userDefines
     */
    public SparkJni setUserDefines(String userDefines) {
        metadataHandler.setUserDefines(userDefines);
        return this;
    }

    /**
     * Set the personalized user directories.
     * @param userLibraryDirs
     */
    public SparkJni setUserLibraryDirs(String userLibraryDirs) {
        metadataHandler.setUserLibraryDirs(userLibraryDirs);
        return this;
    }

    public SparkJni setSparkContext(JavaSparkContext javaSparkContext) {
        this.javaSparkContext = javaSparkContext;
        return this;
    }

    /**
     * Set the personalized user include directories.
     * @param userIncludeDirs
     */
    public SparkJni setUserIncludeDirs(String userIncludeDirs) {
        metadataHandler.setUserIncludeDirs(userIncludeDirs);
        return this;
    }

    public SparkJni setUserLibraries(String userLibraries) {
        metadataHandler.setUserLibraries(userLibraries);
        return this;
    }

    private SparkJni setJdkPath(String jdkPath) {
        metadataHandler.setJdkPath(jdkPath);
        return this;
    }

    private SparkJni setNativePath(String nativePath) {
        metadataHandler.setNativePath(nativePath);
        return this;
    }

    private SparkJni setAppName(String appName) {
        metadataHandler.setAppName(appName);
        return this;
    }

    /**
     * Register the user-defined jni function.
     * @param jniFunctionClass
     */
    public SparkJni registerJniFunction(Class jniFunctionClass) {
        jniLinkHandler.registerJniFunction(jniFunctionClass);
        return this;
    }

    /**
     * Register the user-defined JavaBean container.
     * @param beanClass
     */
    public SparkJni registerContainer(Class beanClass) {
        jniLinkHandler.registerBean(beanClass);
        return this;
    }

    private boolean generateMakefile() {
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

    boolean collectNativeFunctionPrototypes() {
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

    /**
     * @TO-DO Find faster solution and enable it.
     */
    public void loadAnnotatedClasses() {
        ClassLoader sparkJniClassloader = SparkJni.class.getClassLoader();
        try {
            Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(sparkJniClassloader).getTopLevelClasses();
            for (ClassPath.ClassInfo classInfo : classesInPackage) {
                try {
                    Class candidate = Class.forName(classInfo.getName());
                    if (loadJNIContainersAnnotatedClass(candidate))
                        continue;
                    if (loadJNIfuncsAnnotatedClass(candidate))
                        continue;
                } catch (Error err) {
                    System.err.println("Error");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean loadJNIfuncsAnnotatedClass(Class candidate) throws Error {
        Annotation annotation = candidate.getAnnotation(JNI_functionClass.class);
        if (annotation != null) {
            registerJniFunction(candidate);
            System.out.println(String.format("Registered JNI function class %s", candidate.getName()));
            return true;
        }
        return false;
    }

    private boolean loadJNIContainersAnnotatedClass(Class candidate) throws Error {
        Annotation annotation = candidate.getAnnotation(JNI_class.class);
        if (annotation != null) {
            registerContainer(candidate);
            System.out.println(String.format("Registered JNI container class %s", candidate.getName()));
            return true;
        }
        return false;
    }

    public SparkJni setUserStaticLibraries(String userStaticLibraries) {
        metadataHandler.setUserStaticLibraries(userStaticLibraries);
        return this;
    }

    public JniLinkHandler getJniHandler() {
        return jniLinkHandler;
    }

    /**
     * @TODO Complete implementation of the reset feature for persistent environments, where the JVM is not closed
     * between application launches. Or transform to singleton.
     */
    public static void reset() {
        sparkJniSingleton = null;
        MetadataHandler.reset();
        JniLinkHandler.reset();
    }

    public KernelFileWrapperHeader getKernelFileWrapperHeader() {
        return new KernelFileWrapperHeader(jniLinkHandler.getContainerHeaderFiles(), jniRootContainer);
    }

    public KernelFile getKernelFileForHeader(String kernelFileName) {
        return ImmutableKernelFile.builder()
                .jniRootContainer(jniRootContainer)
                .kernelWrapperFileName(kernelFileName)
                .build();
    }

    public JniRootContainer getJniRootContainer() {
        return jniRootContainer;
    }

    public SparkJni setDeployMode(DeployMode deployMode) {
        this.deployMode = deployMode;
        return this;
    }

    public DeployTimesLogger getDeployTimesLogger() {
        return deployTimesLogger;
    }

    public static SparkJni getSparkJniSingleton() {
        return sparkJniSingleton;
    }
}
