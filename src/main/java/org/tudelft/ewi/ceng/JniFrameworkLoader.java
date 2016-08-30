package org.tudelft.ewi.ceng;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.SystemClock;
import org.reflections.Reflections;
import org.tudelft.ewi.ceng.annotations.JNI_class;
import org.tudelft.ewi.ceng.annotations.JNI_functionClass;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Tudor on 7/20/16.
 */
public class JniFrameworkLoader {
    // Constants
    private static final String NEW_MAKEFILE_SECTION = "program_NAME \t\t\t:= %s\n" +
            "program_C_SRCS\t\t\t:= $(wildcard *.c)\n" +
            "program_CPP_SRCS\t\t:= $(wildcard *.cpp)\n" +
            "program_C_OBJS \t\t\t:= ${program_C_SRCS:.c=.o}\n" +
            "JAVA_JDK\t\t\t\t:= %s\n" +
            "program_INCLUDE_DIRS \t:= $(JAVA_JDK)/include $(JAVA_JDK)/include/linux %s\n" +
            "program_LIBRARY_DIRS \t:= %s\n" +
            "program_LIBRARIES \t\t:=  %s\n" +
            "program_STATIC_LIBS\t\t:= %s\n" +
            "DEFINES \t\t\t\t:= %s\n" +
            "DEFINES_LINE\t\t\t:= $(addprefix -D, $(DEFINES))\n" +
            "\n" +
            "CFLAGS \t+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) \n" +
            "CFLAGS\t+= -std=c11 -Wall -m64 -lrt -lpthread -fopenmp -fPIC\n" +
            "CPPFLAGS\t+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) \n" +
            "CPPFLAGS\t+= -shared -fPIC -std=c++11 -O3 -m64 -lrt -lpthread -fopenmp\n" +
            "LDFLAGS \t+= $(foreach librarydir,$(program_LIBRARY_DIRS),-L$(librarydir))\n" +
            "LDFLAGS \t+= $(foreach library,$(program_LIBRARIES),-l$(library))\n" +
            "\n" +
            "all: $(program_NAME)\n" +
            "\n" +
            "debug: CFLAGS += -DDEBUG\n" +
            "debug: $(program_C_OBJS)\n" +
            "\tgcc -o $(program_NAME) $(program_C_OBJS) $(program_STATIC_LIBS) $(LDFLAGS) $(CFLAGS)\n" +
            "\n" +
            "$(program_NAME):$(program_C_OBJS)\n" +
            "\tg++ $(program_CPP_SRCS) -o $(program_NAME).so $(DEFINES_LINE) $(program_STATIC_LIBS) $(CPPFLAGS) $(program_C_OBJS) $(LDFLAGS)\n" +
            "\t\n" +
            "clean:\n" +
            "\t@- $(RM) $(program_NAME)\n" +
            "\t@- $(RM) $(program_C_OBJS)\n" +
            "\n" +
            "distclean: clean";
    private static final String JAVAH_SECTION = "javah -classpath %s -d %s %s";
    private static final String EXEC_MAKE_CLEAN = "make clean -C %s";
    private static final String EXEC_MAKE = "make -C %s";

    // Messages
    private static final String ERROR_JAVAH_FAILED = "[ERROR] javah failed!";
    private static final String NATIVE_PATH_NOT_SET = "[ERROR]Please set native path with JniFrameworkLoader.setNativePath(String path). Exiting..";
    private static final String NATIVE_PATH_ERROR = "[ERROR]Specified native path does not exist or is not a valid directory. Exiting..";
    private static final String MAKEFILE_GENERATION_FAILED_ERROR = "[ERROR]Makefile generation failed. Exiting..";
    private static final String KERNEL_MISSING_NOTICE = "[INFO]Please provide a kernel file";
    private static final String CPP_BUILD_FAILED = "[ERROR]C++ build failed!";
    private static final String ERROR_KERNEL_FILE_GENERATION_FAILED = "[ERROR] Kernel file generation failed..";

    private static String appName;
    private static String userCppKernelFileName;
    private static String libraryFullPath = null;

    // Variables
    private static boolean DEBUGGING_MODE = true;
    private static ArrayList<CppClass> registeredCppContainers = new ArrayList<>();
    private static ArrayList<Class> registeredJavaContainers = new ArrayList<>();
    private static ArrayList<Class> registeredJniFunctions = new ArrayList<>();
    private static TreeMap<String, ArrayList<CppClass>> jniHeaderFunctionPrototypes = new TreeMap<>();
    private static ArrayList<String> jniHeaderFiles = new ArrayList<>();
    // User-side Spark meta-data
    private static String nativePath = null;
    private static String classpath = null;

    private static String userLibraries = "";
    private static String userIncludeDirs = "";
    private static String userLibraryDirs = "";
    private static String userStaticLibraries = "";
    private static String userDefines = "";
    private static boolean doBuild = true;
    private static boolean doGenerateMakefile = false;
    private static JavaSparkContext javaSparkContext = null;
    private static boolean doWriteTemplateFile = false;
    private static String jdkPath = null;
    private static ArrayList<String> containerHeaderFiles = new ArrayList<>();

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

    private JniFrameworkLoader() {}

    public static void deploy(String appName, String kernelFileName, JavaSparkContext sparkContext) {
        long start = System.currentTimeMillis();
        setAppName(appName);
        setUserCppKernelFileName(kernelFileName);
        setSparkContext(sparkContext);
//        searchForClasses();

        if(classpath == null)
            classpath = JniFrameworkLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (nativePath == null) {
            System.err.println(NATIVE_PATH_NOT_SET);
            System.exit(1);
        }

        File nativePathDir = new File(nativePath);
        if (!nativePathDir.exists() || !nativePathDir.isDirectory()) {
            System.err.println(NATIVE_PATH_ERROR);
            System.exit(2);
        }

        CppClass.setNativeLibPath(nativePath);
        if (!generateCppClasses())
            throw new RuntimeException("Cpp File generation failed");

        writeCppHeaderPairs(registeredCppContainers);
        String targetKernelPath = String.format("%s/%s.cpp", nativePath, userCppKernelFileName);
        libraryFullPath = String.format("%s/%s.so", nativePath, appName);

        long startJavah = System.currentTimeMillis();
        javah(classpath);
        collectNativeFunctionPrototypes();
        javahTime = System.currentTimeMillis() - startJavah;

        String templateFilePath = String.format("%s/%s_template.cpp", nativePath, appName);
        KernelFile kernel = new KernelFile(registeredCppContainers, nativePath, templateFilePath,
                jniHeaderFiles, jniHeaderFunctionPrototypes, containerHeaderFiles);

        if (doGenerateMakefile) {
            if (!generateMakefile()) {
                System.err.println(MAKEFILE_GENERATION_FAILED_ERROR);
                System.exit(3);
            }
        }
        if (doWriteTemplateFile) {
            if (!kernel.writeTemplateFile())
                throw new RuntimeException(ERROR_KERNEL_FILE_GENERATION_FAILED);
        }
        genTime = System.currentTimeMillis() - start - javahTime;
        start = System.currentTimeMillis();
        if(doBuild) {
            if (!buildAndLoadKernelLib(targetKernelPath)) {
                System.err.println("Spark context is null. Exiting..");
                System.exit(1);
            }
        }
        buildTime = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();

        if (javaSparkContext != null)
            javaSparkContext.addFile(libraryFullPath);
        else
            System.load(libraryFullPath);
        libLoadTime = System.currentTimeMillis() - start;
    }

    public static void setDoGenerateMakefile(boolean doGenerateMakefile) {
        JniFrameworkLoader.doGenerateMakefile = doGenerateMakefile;
    }

    public static void setDoBuild(boolean doBuild) {
        JniFrameworkLoader.doBuild = doBuild;
    }

    public static void setUserDefines(String userDefines) {
        JniFrameworkLoader.userDefines = userDefines;
    }

    public static void setUserLibraryDirs(String userLibraryDirs) {
        JniFrameworkLoader.userLibraryDirs = userLibraryDirs;
    }

    public static void setSparkContext(JavaSparkContext javaSparkContext) {
        JniFrameworkLoader.javaSparkContext = javaSparkContext;
    }

    public static void setDoWriteTemplateFile(boolean doWriteTemplateFile) {
        JniFrameworkLoader.doWriteTemplateFile = doWriteTemplateFile;
    }

    public static void setUserIncludeDirs(String userIncludeDirs) {
        JniFrameworkLoader.userIncludeDirs = userIncludeDirs;
    }

    public static void setUserLibraries(String userLibraries) {
        JniFrameworkLoader.userLibraries = userLibraries;
    }

    public static String getLibraryFilePath() {
        return libraryFullPath;
    }

    public static void setJdkPath(String jdkPath) {
        JniFrameworkLoader.jdkPath = jdkPath;
    }

    public static void setUserCppKernelFileName(String userCppKernelFileName) {
        if (userCppKernelFileName.endsWith(".cpp"))
            JniFrameworkLoader.userCppKernelFileName = userCppKernelFileName.substring(0, userCppKernelFileName.length() - 4);
        else
            JniFrameworkLoader.userCppKernelFileName = userCppKernelFileName;
    }

    public static void setNativePath(String nativePath) {
        JniFrameworkLoader.nativePath = nativePath;
    }

    public static void setAppName(String appName) {
        JniFrameworkLoader.appName = appName;
    }

    public static void registerJniFunction(Class jniFunctionClass) {
        registeredJniFunctions.add(jniFunctionClass);
    }

    private static boolean buildAndLoadKernelLib(String targetKernelPath){
        if (new File(targetKernelPath).exists()) {
            runProcess(String.format(EXEC_MAKE_CLEAN, nativePath));
            runProcess(String.format(EXEC_MAKE, nativePath));
        } else {
            System.out.println(KERNEL_MISSING_NOTICE);
            System.exit(0);
            return false;
        }

        return true;
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

        String newMakefileContent = String.format(NEW_MAKEFILE_SECTION,
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

    public static boolean searchForClasses() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(JNI_class.class);

        for (Class jniClass : containers)
            registerContainer(jniClass);

        Set<Class<?>> jniFunctions = reflections.getTypesAnnotatedWith(JNI_functionClass.class);
        for (Class jniFunction : jniFunctions) {
            registerJniFunction(jniFunction);
        }
        return true;
    }

    public static boolean collectNativeFunctionPrototypes() {
        // For the moment we assume we only have javah-generated header files
        // Iterate through them and implement the native functions.
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

    public static void generateCppKernelWrapper() {

    }

    public static void generateKernelHeader() {
        StringBuilder sb = new StringBuilder();
    }

    private static void javah(String classpath) {
        for (Class jniFunctionClass : registeredJniFunctions) {
            String javah = String.format(JAVAH_SECTION, classpath, nativePath,
                    jniFunctionClass.getName());

            try {
                Process processJavah = Runtime.getRuntime().exec(javah);
                if (processJavah.waitFor() != 0)
                    throw new RuntimeException(ERROR_JAVAH_FAILED);
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
                throw new RuntimeException(CPP_BUILD_FAILED);
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
}
