package org.tudelft.ewi.ceng.sparkjni.javaLink;

import org.tudelft.ewi.ceng.sparkjni.jniFunctions.JniFunction;
import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;
import org.tudelft.ewi.ceng.sparkjni.utils.MetadataHandler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by root on 9/21/16.
 */
public class JniLinkHandler {
    private String nativePath;
    private ArrayList<Class> registeredJavaContainers = new ArrayList<>();
    private ArrayList<Class> registeredJniFunctions = new ArrayList<>();
    private ArrayList<CppClass> registeredCppContainers = new ArrayList<>();

    private ArrayList<String> jniHeaderFiles = new ArrayList<>();
    private ArrayList<String> containerHeaderFiles = new ArrayList<>();

    private ArrayList<KernelFile> kernelFiles = new ArrayList<>();
    private TreeMap<String, ArrayList<CppClass>> jniHeaderFunctionPrototypes = new TreeMap<>();

    public boolean isDEBUGGING_MODE() {
        return DEBUGGING_MODE;
    }

    public void setDEBUGGING_MODE(boolean DEBUGGING_MODE) {
        this.DEBUGGING_MODE = DEBUGGING_MODE;
    }

    private boolean DEBUGGING_MODE = true;

    public ArrayList<Class> getRegisteredJavaContainers() {
        return registeredJavaContainers;
    }

    public ArrayList<Class> getRegisteredJniFunctions() {
        return registeredJniFunctions;
    }

    public ArrayList<CppClass> getRegisteredCppContainers() {
        return registeredCppContainers;
    }

    public ArrayList<String> getJniHeaderFiles() {
        return jniHeaderFiles;
    }

    public ArrayList<String> getContainerHeaderFiles() {
        return containerHeaderFiles;
    }

    public ArrayList<KernelFile> getKernelFiles() {
        return kernelFiles;
    }

    public TreeMap<String, ArrayList<CppClass>> getJniHeaderFunctionPrototypes() {
        return jniHeaderFunctionPrototypes;
    }

    public JniLinkHandler() {
        this.nativePath = MetadataHandler.getHandler().getNativePath();
    }

    public void registerJniFunction(Class jniFunctionClass){
//        if(!jniFunctionClass.getSuperclass().equals(JniFunction.class))
//            throw new RuntimeException("JNI function class does not inherit JniFunction");
        registeredJniFunctions.add(jniFunctionClass);
    }

    public void registerBean(Class beanClass){
        registeredJavaContainers.add(beanClass);
    }

    public void deployLink(){
        if (!generateCppClasses())
            throw new RuntimeException(JniUtils.ERR_CPP_FILE_GENERATION_FAILED);
        writeCppHeaderPairs();
    }

    private boolean generateCppClasses() {
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

        if (!registeredJavaContainers.isEmpty()) {
            for(Class unlinked: registeredJavaContainers)
                System.out.println(String.format("Unmapped registered Java container %s", unlinked.getSimpleName()));
            return false;
        }
        else
            return true;
    }

    private void writeCppHeaderPairs() {
        for (CppClass cppClass : registeredCppContainers) {
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

    public void javah(String classpath) {
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

    public CppClass getContainerByJavaClass(Class javaClass) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getJavaClass().getName().equals(javaClass.getName()))
                return cppClass;
        return null;
    }

    public CppClass getContainerByClassName(String className) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getJavaClass().getName().equals(className))
                return cppClass;
        return null;
    }

    public ArrayList<CppClass> getNativeMethodParams(String methodName) {
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

    public boolean isTypeRegistered(String simpleTypeName) {
        for (CppClass cppClass : registeredCppContainers)
            if (cppClass.getCppClassName().equals(simpleTypeName))
                return true;
        return false;
    }

    public void registerNativePrototype(String line, String methodName){
        jniHeaderFunctionPrototypes.put(line, getNativeMethodParams(methodName));
    }
}
