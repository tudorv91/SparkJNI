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
package org.tudelft.ewi.ceng.sparkjni.utils;

import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Top level class with static functionality that handles the SparkJNI execution.
 */
public class JniFrameworkLoader {
    public static final String LIB_PATH_STR = "%s/%s.so";
    private static boolean DEBUGGING_MODE = true;
    private static boolean doClean = true;
    private static boolean doBuild = true;
    private static boolean doGenerateMakefile = false;
    private static boolean doWriteKernelFiles = false;

    private static String appName;
    private static String nativePath;
    private static String classpath;
    private static String jdkPath;
    private static String userLibraries;
    private static String userIncludeDirs;
    private static String userLibraryDirs;
    private static String userStaticLibraries;
    private static String userDefines;

    private static ArrayList<Class> registeredJavaContainers = new ArrayList<>();
    private static ArrayList<Class> registeredJniFunctions = new ArrayList<>();
    private static ArrayList<String> jniHeaderFiles = new ArrayList<>();
    private static ArrayList<String> containerHeaderFiles = new ArrayList<>();
    private static ArrayList<CppClass> registeredCppContainers = new ArrayList<>();
    private static ArrayList<KernelFile> kernelFiles = new ArrayList<>();
    private static TreeMap<String, ArrayList<CppClass>> jniHeaderFunctionPrototypes = new TreeMap<>();

    private static JavaSparkContext javaSparkContext = null;

    private static long start = 0L;
    private static long genTime = 0L;
    private static long buildTime = 0L;
    private static long libLoadTime = 0L;
    private static long javahTime = 0L;

    public static long getStart() {
        return start;
    }
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

    private JniFrameworkLoader() {}

    /**
     * Function which triggers the SparkJNI processes.
     * @param appName The target app name.
     * @param sparkContext The Spark Context. Leave null if running without Spark.
     */
    public static void deploy(String appName, JavaSparkContext sparkContext) {
        start = System.currentTimeMillis();
        setAppName(appName);
        setSparkContext(sparkContext);
        loadClasspathVar();
        processNativePath();
        processCppContent();
        loadNativeLib();
    }

    private static void loadNativeLib(){
        String libraryFullPath = String.format(LIB_PATH_STR, nativePath, appName);
        if (javaSparkContext != null)
            javaSparkContext.addFile(libraryFullPath);
        else
            System.load(libraryFullPath);
        libLoadTime = System.currentTimeMillis() - start;
    }

    private static void processNativePath(){
        checkNativePath(nativePath);
        checkNativePathDir(new File(nativePath));
        CppClass.setNativeLibPath(nativePath);
    }

    private static void processCppContent(){
        cleanHeaderFiles();
        if (!generateCppClasses())
            throw new RuntimeException(JniUtils.ERR_CPP_FILE_GENERATION_FAILED);

        writeCppHeaderPairs(registeredCppContainers);

        // TO-DO: Populate kernel files.
        generateAndCheckMakefile();
        writeKernelFiles();
        build();

        long startJavah = System.currentTimeMillis();
        javah(classpath);
        collectNativeFunctionPrototypes();
        javahTime = System.currentTimeMillis() - startJavah;
    }

    private static void cleanHeaderFiles(){
        JniDirAccessor dirAccessor = new JniDirAccessor(nativePath);
        for(JniHeader header: dirAccessor.getJniHeaders())
            header.removeHeaderFile();
    }

    private static void loadClasspathVar(){
        if(classpath == null)
            classpath = JniFrameworkLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    static void writeKernelFiles(){
        if (doWriteKernelFiles)
            for(KernelFile kernelFile: kernelFiles)
                if (!kernelFile.writeKernelFile())
                    throw new RuntimeException(JniUtils.ERROR_KERNEL_FILE_GENERATION_FAILED);
    }

    static void generateAndCheckMakefile(){
        if (doGenerateMakefile) {
            if (!generateMakefile()) {
                System.err.println(JniUtils.MAKEFILE_GENERATION_FAILED_ERROR);
                System.exit(3);
            }
        }
    }

    static void build(){
        genTime = System.currentTimeMillis() - start - javahTime;
        start = System.currentTimeMillis();
        if(doBuild) {
            runProcess(String.format(JniUtils.EXEC_MAKE_CLEAN, nativePath));
            runProcess(String.format(JniUtils.EXEC_MAKE, nativePath));
        }
        buildTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
    }

    private static void checkNativePath(String nativePath){
        if (nativePath == null) {
            System.err.println(JniUtils.NATIVE_PATH_NOT_SET);
            System.exit(1);
        }
    }

    private static void checkNativePathDir(File nativePathDir){
        if (!nativePathDir.exists() || !nativePathDir.isDirectory()) {
            System.err.println(JniUtils.NATIVE_PATH_ERROR);
            System.exit(2);
        }
    }

    /**
     * Enable automatic makefile generation at the end of the deployment stage.
     * @param doGenerateMakefile
     */
    public static void setDoGenerateMakefile(boolean doGenerateMakefile) {
        JniFrameworkLoader.doGenerateMakefile = doGenerateMakefile;
    }

    /**
     * Enable building the shared native library.
     * @param doBuild
     */
    public static void setDoBuild(boolean doBuild) {
        JniFrameworkLoader.doBuild = doBuild;
    }

    /**
     * Set the user defines pragma for the build stage flags.
     * @param userDefines
     */
    public static void setUserDefines(String userDefines) {
        JniFrameworkLoader.userDefines = userDefines;
    }

    /**
     * Set the personalized user directories.
     * @param userLibraryDirs
     */
    public static void setUserLibraryDirs(String userLibraryDirs) {
        JniFrameworkLoader.userLibraryDirs = userLibraryDirs;
    }

    public static void setSparkContext(JavaSparkContext javaSparkContext) {
        JniFrameworkLoader.javaSparkContext = javaSparkContext;
    }

    /**
     * Trigger the writing of the template file.
     * @param doWriteKernelFiles
     */
    public static void setDoWriteKernelFiles(boolean doWriteKernelFiles) {
        JniFrameworkLoader.doWriteKernelFiles = doWriteKernelFiles;
    }

    /**
     * Set the personalized user include directories.
     * @param userIncludeDirs
     */
    public static void setUserIncludeDirs(String userIncludeDirs) {
        JniFrameworkLoader.userIncludeDirs = userIncludeDirs;
    }

    public static void setUserLibraries(String userLibraries) {
        JniFrameworkLoader.userLibraries = userLibraries;
    }

    public static void setJdkPath(String jdkPath) {
        JniFrameworkLoader.jdkPath = jdkPath;
    }

    public static void setNativePath(String nativePath) {
        JniFrameworkLoader.nativePath = nativePath;
    }

    public static void setAppName(String appName) {
        JniFrameworkLoader.appName = appName;
    }

    /**
     * Register the user-defined jni function.
     * @param jniFunctionClass
     */
    public static void registerJniFunction(Class jniFunctionClass) {
        registeredJniFunctions.add(jniFunctionClass);
    }

    private static void writeCppHeaderPairs(ArrayList<CppClass> registeredContainers) {
        for (CppClass cppClass : registeredContainers) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(cppClass.getCppFilePath());
                writer.write(cppClass.getCppImplementation());

                writer.close();

                writer = new PrintWriter(cppClass.getHeaderFilePath());
                writer.write(cppClass.getHeaderImplementation());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.exit(5);
            } finally {
                if (writer != null)
                    writer.close();
            }
        }
    }

    /**
     * Register the user-defined Bean container.
     * @param beanClass
     */
    public static void registerContainer(Class beanClass) {
        registeredJavaContainers.add(beanClass);
    }

    private static boolean generateCppClasses() {
        int noContainers = registeredJavaContainers.size();
        int maxIters = noContainers * noContainers;
        for (int iterIdx = 0; iterIdx < maxIters && registeredJavaContainers.size() > 0; iterIdx++) {
            for (int idx = 0; idx < registeredJavaContainers.size(); idx++) {
                Class javaContainer = registeredJavaContainers.get(idx);
                CppClass cppClass = new CppClass(javaContainer);
                if (cppClass.isSuccesful()) {
                    registeredJavaContainers.remove(idx);
                    registeredCppContainers.add(cppClass);
                    containerHeaderFiles.add(cppClass.getCppClassName() + ".h");
                    if (DEBUGGING_MODE) {
                        System.out.println(cppClass.getCppFilePath() + " : ");
                        System.out.println(cppClass.getCppImplementation());
                    }
                    break;
                }
            }
        }

        if (CppClass.getNativeLibPath() == null) {
            CppClass.setNativeLibPath(nativePath);
        }

        if (!registeredJavaContainers.isEmpty()) {
            for(Class unlinked: registeredJavaContainers)
                System.out.println(String.format("Unmapped registered Java container %s", unlinked.getSimpleName()));
            return false;
        }
        else
            return true;
    }

    private static boolean generateMakefile() {
        if (jdkPath == null || jdkPath.isEmpty())
            return false;

        String newMakefileContent = String.format(JniUtils.NEW_MAKEFILE_SECTION,
                appName, jdkPath, userIncludeDirs, userLibraryDirs,
                userLibraries, userStaticLibraries, userDefines);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(nativePath + "/Makefile");
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
        File nativeLibDir = new File(nativePath);

        if (nativeLibDir.exists() && nativeLibDir.isDirectory()) {
            File[] headerFiles = nativeLibDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return s.endsWith(".h");
                }
            });
            for (File headerFile : headerFiles) {
                jniHeaderFiles.add(headerFile.getName());
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
                            jniHeaderFunctionPrototypes.put(line, getNativeMethodParams(methodName));
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

    private static void javah(String classpath) {
        for (Class jniFunctionClass : registeredJniFunctions) {
            String javah = String.format(JniUtils.JAVAH_SECTION, classpath, nativePath,
                    jniFunctionClass.getName());

            try {
                Process processJavah = Runtime.getRuntime().exec(javah);
                if (processJavah.waitFor() != 0)
                    throw new RuntimeException(JniUtils.ERROR_JAVAH_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void runProcess(String proc) {
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
                throw new RuntimeException(JniUtils.CPP_BUILD_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static CppClass getContainerByJavaClass(Class javaClass) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getJavaClass().getName().equals(javaClass.getName()))
                return cppClass;
        return null;
    }

    public static CppClass getContainerByClassName(String className) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getJavaClass().getName().equals(className))
                return cppClass;
        return null;
    }

    public static ArrayList<CppClass> getNativeMethodParams(String methodName) {
        ArrayList<CppClass> cppParameters = new ArrayList<>();
        for (Class jniFunction : registeredJniFunctions) {
            try {
                Method[] methods = jniFunction.getMethods();
                Method paramTypeMethod = null;
                for (Method method : methods) {
                    if (method.getName().equals("getParamTypes"))
                        paramTypeMethod = method;
                }
                ArrayList<Class> paramTypes = (ArrayList<Class>) paramTypeMethod.invoke(jniFunction.newInstance(), methodName);
                for (Class paramType : paramTypes)
                    cppParameters.add(getContainerByJavaClass(paramType));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(7);
            }
        }
        return cppParameters;
    }

    public static String getUserStaticLibraries() {
        return userStaticLibraries;
    }

    public static void setUserStaticLibraries(String userStaticLibraries) {
        JniFrameworkLoader.userStaticLibraries = userStaticLibraries;
    }

    public static boolean isTypeRegistered(String simpleTypeName) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getCppClassName().equals(simpleTypeName))
                return true;

        return false;
    }

    public static boolean isDoClean() {
        return doClean;
    }
}
