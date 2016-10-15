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
package org.heterojni.sparkjni.utils;

import com.google.common.base.Optional;
import com.google.common.reflect.ClassPath;
import org.apache.spark.api.java.JavaSparkContext;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.heterojni.sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import org.heterojni.sparkjni.jniLink.linkHandlers.KernelFile;
import org.heterojni.sparkjni.jniLink.linkHandlers.KernelFileWrapperHeader;
import org.heterojni.sparkjni.jniLink.linkHandlers.UserNativeFunction;
import org.heterojni.sparkjni.utils.exceptions.HardSparkJniException;
import org.heterojni.sparkjni.utils.exceptions.Messages;
import org.heterojni.sparkjni.utils.exceptions.SoftSparkJniException;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_class;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_functionClass;
import org.immutables.builder.Builder;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Top level class with static functionality that handles the SparkJNI execution.
 */
public class SparkJni {
    private boolean doForceOverwriteKernelFiles = true;
    private boolean doClean = true;
    private boolean doBuild = true;
    private boolean doGenerateMakefile = true;

    private static JniLinkHandler jniLinkHandler;
    private static MetadataHandler metadataHandler;

    private static JniRootContainer jniRootContainer;

    private static JavaSparkContext javaSparkContext;
    private long start = 0L;
    private long genTime = 0L;

    private long buildTime = 0L;
    private long libLoadTime = 0L;
    private long javahTime = 0L;
    private static SparkJni sparkJniSingleton = null;

    private HashMap<String, String> functionCodeInjectorMap;

    public SparkJni doWriteLinkClasses(boolean writeLinkClasses) {
        this.writeLinkClasses = writeLinkClasses;
        return this;
    }

    private boolean writeLinkClasses = true;

    private SparkJni(){}

    @Builder.Factory
    public static SparkJni sparkJniSingleton(String appName, String nativePath, String jdkPath) {
        if(sparkJniSingleton == null){
            sparkJniSingleton = new SparkJni();
            sparkJniSingleton.initVars(appName, nativePath, jdkPath);
        }
        return sparkJniSingleton;
    }

    public static SparkJni getSparkJniSingleton(){
        return sparkJniSingleton;
    }

    private void initVars(String appName, String nativePath, String jdkPath){
        metadataHandler = MetadataHandler.getHandler();
        setAppName(appName);
        setNativePath(nativePath);
        setJdkPath(jdkPath);
        jniLinkHandler = JniLinkHandler.getJniLinkHandlerSingleton();
    }

    public void deploy() {
        start = System.currentTimeMillis();
        processCppContent();
        loadNativeLib();
    }

    public void deployWithCodeInjections(HashMap<String, String> functionCodeInjectorMap){
        this.functionCodeInjectorMap = functionCodeInjectorMap;
        deploy();
    }

    private void loadNativeLib(){
        String libraryFullPath = JniUtils.generateDefaultLibPath(metadataHandler.getAppName(), metadataHandler.getNativePath());
        if (javaSparkContext != null)
            javaSparkContext.addFile(libraryFullPath);
        else
            System.load(libraryFullPath);
        libLoadTime = System.currentTimeMillis() - start;
    }

    private void processCppContent(){
        checkNativePath();
        try {
            cleanHeaderFiles();
        } catch (SoftSparkJniException ex){
            ex.printStackTrace();
        }

        if(jniLinkHandler != null)
            jniLinkHandler.deployLink(writeLinkClasses);
        else
            throw new RuntimeException("NOT SET");

        long startJavah = System.currentTimeMillis();
        jniLinkHandler.javah(JniUtils.getClasspath());
        collectNativeFunctionPrototypes();
        javahTime = System.currentTimeMillis() - startJavah;

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

    private void cleanHeaderFiles() throws SoftSparkJniException{
        File nativeDir = new File(metadataHandler.getNativePath());
        if(nativeDir.isDirectory()) {
            for (File file : nativeDir.listFiles()) {
                try {
                    if (JniUtils.isJniNativeFunction(file.toPath()))
                        file.delete();
                } catch (IOException ex) {
                }
            }
        }
    }

    public void generateKernelFiles(){
        KernelFileWrapperHeader kernelFileWrapperHeader = getKernelFileWrapperHeader();
        if (kernelFileWrapperHeader == null || !kernelFileWrapperHeader.writeKernelWrapperFile())
            throw new HardSparkJniException(Messages.ERR_KERNEL_FILE_GENERATION_FAILED);
        if(doForceOverwriteKernelFiles) {
            KernelFile kernelFile = kernelFileWrapperHeader.getKernelFile();
            if(functionCodeInjectorMap != null && !functionCodeInjectorMap.isEmpty())
                injectFunctionCodeBody(kernelFile.userNativeFunctions());
            kernelFile.writeKernelFile();
        }
    }

    private void injectFunctionCodeBody(List<UserNativeFunction> userNativeFunctions) {
        for(UserNativeFunction userNativeFunction: userNativeFunctions){
            String functionName = userNativeFunction.functionSignatureMapper().functionNameMapper().cppName();
            String codeBody = functionCodeInjectorMap.get(functionName);
            if(codeBody == null)
                continue;
            userNativeFunction.setFunctionBodyCodeInsertion(Optional.of(codeBody));
        }
    }

    void generateAndCheckMakefile(){
        if (doGenerateMakefile)
            if (!generateMakefile()) {
                System.err.println(Messages.MAKEFILE_GENERATION_FAILED_ERROR);
                System.exit(3);
            }
    }

    void build(){
        String nativePath = metadataHandler.getNativePath();
        genTime = System.currentTimeMillis() - start - javahTime;
        start = System.currentTimeMillis();
        if(doBuild) {
            JniUtils.runProcess(String.format(CppSyntax.EXEC_MAKE_CLEAN, nativePath));
            JniUtils.runProcess(String.format(CppSyntax.EXEC_MAKE, nativePath));
        }
        buildTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
    }

    private void checkNativePath(){
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
    public SparkJni setDoGenerateMakefile(boolean doGenerateMakefile) {
        this.doGenerateMakefile = doGenerateMakefile;
        return this;
    }

    /**
     * Enable building the shared native library.
     * @param doBuild
     */
    public SparkJni setDoBuild(boolean doBuild) {
        this.doBuild = doBuild;
        return this;
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
     * Trigger the writing of the template file.
     * @param doForceOverwriteKernelFiles
     */
    public SparkJni setDoForceOverwriteKernelFiles(boolean doForceOverwriteKernelFiles) {
        this.doForceOverwriteKernelFiles = doForceOverwriteKernelFiles;
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

    public boolean collectNativeFunctionPrototypes() {
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
    public void loadAnnotatedClasses(){
        ClassLoader sparkJniClassloader = SparkJni.class.getClassLoader();
        try {
            Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(sparkJniClassloader).getTopLevelClasses();
            for(ClassPath.ClassInfo classInfo: classesInPackage){
                try {
                    Class candidate = Class.forName(classInfo.getName());
                    if(loadJNIContainersAnnotatedClass(candidate))
                        continue;
                    if(loadJNIfuncsAnnotatedClass(candidate))
                        continue;
                } catch(Error err){
                    System.err.println("Error");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean loadJNIfuncsAnnotatedClass(Class candidate) throws Error{
        Annotation annotation = candidate.getAnnotation(JNI_functionClass.class);
        if (annotation != null) {
            registerJniFunction(candidate);
            System.out.println(String.format("Registered JNI function class %s", candidate.getName()));
            return true;
        }
        return false;
    }

    private boolean loadJNIContainersAnnotatedClass(Class candidate) throws Error{
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

    public boolean isDoClean() {
        return doClean;
    }

    public JniLinkHandler getJniHandler() {
        return jniLinkHandler;
    }

    public long getStart() {
        return start;
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

    public boolean isDoForceOverwriteKernelFiles() {
        return doForceOverwriteKernelFiles;
    }

    public long getJavahTime() {
        return javahTime;
    }
    public long getLibLoadTime() {
        return libLoadTime;
    }
    public long getGenTime() {
        return genTime;
    }
    public long getBuildTime() {
        return buildTime;
    }

    public KernelFileWrapperHeader getKernelFileWrapperHeader() {
        return new KernelFileWrapperHeader(jniLinkHandler.getContainerHeaderFiles(), jniRootContainer);
    }

    public JniRootContainer getJniRootContainer() {
        return jniRootContainer;
    }
}
