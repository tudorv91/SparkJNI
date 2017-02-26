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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.apache.spark.api.java.JavaSparkContext;
import org.immutables.builder.Builder;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import sparkjni.jniLink.linkHandlers.KernelFile;
import sparkjni.jniLink.linkHandlers.KernelFileWrapperHeader;
import sparkjni.jniLink.linkHandlers.UserNativeFunction;
import sparkjni.utils.exceptions.HardSparkJniException;
import sparkjni.utils.exceptions.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import static sparkjni.utils.AppInjector.injectSparkJni;

public class SparkJni {
    private final MetadataHandler metadataHandler;
    private final DeployTimesLogger deployTimesLogger;
    private final JniLinkHandler jniLinkHandler;
    private final MakefileGenerator makefileGenerator;
    private final NativeFunctionPrototypesCollector nativeFunctionPrototypesCollector;

    private static JniRootContainer jniRootContainer;
    private static JavaSparkContext javaSparkContext;
    private HashMap<String, String> functionCodeInjectorMap;

    private DeployMode deployMode;
    private boolean overWriteKernelFile = false;

    @Inject
    private SparkJni(@Nonnull MetadataHandler metadataHandler, @Nonnull DeployTimesLogger deployTimesLogger, @Nonnull Provider<JniLinkHandler> jniLinkHandlerProvider,
                     @Nonnull MakefileGenerator makefileGenerator, @Nonnull NativeFunctionPrototypesCollector nativeFunctionPrototypesCollector) {
        this.metadataHandler = metadataHandler;
        this.deployTimesLogger = deployTimesLogger;
        this.jniLinkHandler = jniLinkHandlerProvider.get();
        this.makefileGenerator = makefileGenerator;
        this.nativeFunctionPrototypesCollector = nativeFunctionPrototypesCollector;
        // by default, follow the entire deploy process
        deployMode = new DeployMode(DeployMode.DeployModes.FULL_GENERATE_AND_BUILD);
    }

    @Builder.Factory
    static SparkJni sparkJni(@Nonnull Optional<String> appName, @Nonnull String nativePath, @Nonnull Optional<String> jdkPath, @Nonnull Optional<String> classpath) {
        final SparkJni sparkJniSingleton = injectSparkJni();
        sparkJniSingleton.initVars(appName.isPresent() ? appName.get() : null, nativePath, jdkPath.isPresent() ? jdkPath.get() : null);
        classpath.transform(new Function<String, Object>() {
            @Nullable
            @Override
            public Object apply(@Nullable String s) {
                sparkJniSingleton.addToClasspath(s);
                return new Object();
            }
        });
        return sparkJniSingleton;
    }

    private void initVars(String appName, String nativePath, String jdkPath) {
        setAppName(appName);
        setNativePath(nativePath);
        setJdkPath(jdkPath);
    }

    public void deploy() {
        deployTimesLogger.start = System.currentTimeMillis();
        processCppContent();
        loadNativeLib();
    }

    public void deployWithCodeInjections(HashMap<String, String> functionCodeInjectorMap) {
        if (!functionCodeInjectorMap.isEmpty()) {
            this.functionCodeInjectorMap = functionCodeInjectorMap;
        }
        deploy();
    }

    private void loadNativeLib() {
        String libraryFullPath = JniUtils.generateDefaultLibPath(metadataHandler.getAppName(), metadataHandler.getNativePath());
        if (javaSparkContext != null) {
            javaSparkContext.addFile(libraryFullPath);
        }
        else {
            System.load(libraryFullPath);
        }
        deployTimesLogger.libLoadTime = System.currentTimeMillis() - deployTimesLogger.start;
    }

    private void processCppContent() {
        checkNativePath();
        jniLinkHandler.deployLink();
        executeAndBenchmarkJavah();
        generateAndCheckMakefile();
        generateJniRootContainer();
        generateKernelFiles();
        build();
    }

    private void executeAndBenchmarkJavah() {
        long startJavah = System.currentTimeMillis();
        if (deployMode.doJavah) {
            jniLinkHandler.javah(metadataHandler.getClasspath());
        }
        nativeFunctionPrototypesCollector.collectNativeFunctionPrototypes();
        deployTimesLogger.javahTime = System.currentTimeMillis() - startJavah;
    }

    public void addToClasspath(String... classpath) {
        for (String cPath : classpath) {
            metadataHandler.addToClasspath(cPath);
        }
    }

    private void generateJniRootContainer() {
        jniRootContainer = ImmutableJniRootContainerProvider.builder().build()
                .buildJniRootContainer(metadataHandler.getNativePath(), metadataHandler.getAppName());
    }

    private void generateKernelFiles() {
        KernelFileWrapperHeader kernelFileWrapperHeader = getKernelFileWrapperHeader();
        if (!deployMode.doForceOverwriteKernelWrappers) {
            return;
        }
        if (!kernelFileWrapperHeader.writeKernelWrapperFile()) {
            throw new HardSparkJniException(Messages.ERR_KERNEL_FILE_GENERATION_FAILED);
        }
        if (deployMode.doForceOverwriteKernelWrappers) {
            KernelFile kernelFile = kernelFileWrapperHeader.getKernelFile();
            if (functionCodeInjectorMap != null && !functionCodeInjectorMap.isEmpty()) {
                injectFunctionCodeBody(kernelFile.userNativeFunctions());
            }
            kernelFile.writeKernelFile(overWriteKernelFile);
        }
    }

    private void injectFunctionCodeBody(List<UserNativeFunction> userNativeFunctions) {
        for (UserNativeFunction userNativeFunction : userNativeFunctions) {
            String functionName = userNativeFunction.functionSignatureMapper().functionNameMapper().cppName();
            String codeBody = functionCodeInjectorMap.get(functionName);
            if (codeBody == null)
                continue;
            userNativeFunction.setFunctionBodyCodeInsertion(Optional.of(codeBody));
        }
    }

    private void generateAndCheckMakefile() {
        if (deployMode.doGenerateMakefile)
            if (!makefileGenerator.generateMakefile(deployMode)) {
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
            System.err.println(Messages.NATIVE_PATH_ERROR + ":" + nativePathDir.getAbsolutePath());
            System.exit(2);
        }
    }

    public void registerClassifier(SparkJniClassifier sparkJniClassifier) {
        for (Class functionClass : sparkJniClassifier.getJniFunctionClasses()) {
            registerJniFunction(functionClass);
        }
        for (Class beanClass : sparkJniClassifier.getBeanClasses()) {
            registerContainer(beanClass);
        }
    }

    /**
     * Set the user defines pragma for the build stage flags.
     *
     * @param userDefines
     */
    @SuppressWarnings("unused")
    public SparkJni setUserDefines(String userDefines) {
        metadataHandler.setUserDefines(userDefines);
        return this;
    }

    /**
     * Set the personalized user directories.
     *
     * @param userLibraryDirs
     */
    @SuppressWarnings("unused")
    public SparkJni setUserLibraryDirs(String userLibraryDirs) {
        metadataHandler.setUserLibraryDirs(userLibraryDirs);
        return this;
    }

    public SparkJni setSparkContext(JavaSparkContext javaSparkContext) {
        SparkJni.javaSparkContext = javaSparkContext;
        return this;
    }

    /**
     * Set the personalized user include directories.
     *
     * @param userIncludeDirs
     */
    public SparkJni setUserIncludeDirs(String userIncludeDirs) {
        metadataHandler.setUserIncludeDirs(userIncludeDirs);
        return this;
    }

    @SuppressWarnings("unused")
    public SparkJni setUserLibraries(String userLibraries) {
        metadataHandler.setUserLibraries(userLibraries);
        return this;
    }

    public SparkJni setJdkPath(String jdkPath) {
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
     *
     * @param jniFunctionClass
     */
    public SparkJni registerJniFunction(Class jniFunctionClass) {
        jniLinkHandler.registerJniFunction(jniFunctionClass);
        return this;
    }

    /**
     * Register the user-defined JavaBean container.
     *
     * @param beanClass
     */
    public SparkJni registerContainer(Class beanClass) {
        jniLinkHandler.registerBean(beanClass);
        return this;
    }

    public JniLinkHandler getJniHandler() {
        return jniLinkHandler;
    }

    private KernelFileWrapperHeader getKernelFileWrapperHeader() {
        return new KernelFileWrapperHeader(jniLinkHandler.getContainerHeaderFiles(), jniRootContainer);
    }

    public JniRootContainer getJniRootContainer() {
        return jniRootContainer;
    }

    public SparkJni setDeployMode(DeployMode deployMode) {
        this.deployMode = deployMode;
        return this;
    }

    @SuppressWarnings("unused")
    public DeployTimesLogger getDeployTimesLogger() {
        return deployTimesLogger;
    }

    public DeployMode getDeployMode() {
        return deployMode;
    }

    ClassLoader getClassloader() {
        return metadataHandler.getClassloader();
    }

    public void setClassloader(ClassLoader classloader) {
        metadataHandler.setClassloader(classloader);
    }

    public void setOverwriteKernelFile(boolean overwriteKernelFile) {
        this.overWriteKernelFile = overwriteKernelFile;
    }
}
