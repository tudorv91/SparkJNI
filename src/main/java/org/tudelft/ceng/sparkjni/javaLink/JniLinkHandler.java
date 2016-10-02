package org.tudelft.ceng.sparkjni.javaLink;

import org.tudelft.ceng.sparkjni.exceptions.HardSparkJniException;
import org.tudelft.ceng.sparkjni.jniFunctions.JniFunction;
import org.tudelft.ceng.sparkjni.utils.*;
import org.tudelft.ceng.sparkjni.exceptions.Messages;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by root on 9/21/16.
 */
public class JniLinkHandler {
    private static JniLinkHandler jniLinkHandlerSingleton;
    private ArrayList<Class> registeredJavaContainers = new ArrayList<>();
    private ArrayList<Class> registeredJniFunctions = new ArrayList<>();
    private ArrayList<CppBean> registeredCppContainers = new ArrayList<>();

    private ArrayList<String> jniHeaderFiles = new ArrayList<>();
    private ArrayList<String> containerHeaderFiles = new ArrayList<>();

    private TreeMap<String, ArrayList<CppBean>> jniHeaderFunctionPrototypes = new TreeMap<>();

    private boolean DEBUGGING_MODE = true;

    private JniLinkHandler(){}

    public static JniLinkHandler getJniLinkHandlerSingleton(){
        if(jniLinkHandlerSingleton == null)
            jniLinkHandlerSingleton = new JniLinkHandler();
        return jniLinkHandlerSingleton;
    }

    public KernelFile getKernelFile() {
        MetadataHandler metadataHandler = MetadataHandler.getHandler();
        JniDirAccessor jniDirAccessor = new JniDirAccessor(MetadataHandler.getHandler().getNativePath());
        List<JniHeaderHandler> headers = jniDirAccessor.getJniHeaderHandlers();
        String targetKernelFilePath = metadataHandler.getNativePath() + "/" + metadataHandler.getAppName()+".cpp";
        return new KernelFile(targetKernelFilePath, jniHeaderFiles, containerHeaderFiles, headers);
    }

    public TreeMap<String, ArrayList<CppBean>> getJniHeaderFunctionPrototypes() {
        return jniHeaderFunctionPrototypes;
    }

    public void registerJniFunction(Class jniFunctionClass){
        if(!JniFunction.class.isAssignableFrom(jniFunctionClass))
            throw new HardSparkJniException(Messages.ERR_JNI_FUNCTION_CLASS_DOES_NOT_INHERIT_JNI_FUNCTION);
        registeredJniFunctions.add(jniFunctionClass);
    }

    public void registerBean(Class beanClass){
        registeredJavaContainers.add(beanClass);
    }

    public void deployLink(){
        if (!generateCppBeanClasses())
            throw new RuntimeException(Messages.ERR_CPP_FILE_GENERATION_FAILED);
        writeCppHeaderPairs();
    }

    private boolean generateCppBeanClasses() {
        int maxIters = (int) Math.pow(registeredJavaContainers.size(), 2);
        if(CppBean.getNativeLibPath()==null)
            CppBean.setNativeLibPath(MetadataHandler.getHandler().getNativePath());
        for (int iterIdx = 0; iterIdx < maxIters && registeredJavaContainers.size() > 0; iterIdx++) {
            for (int idx = 0; idx < registeredJavaContainers.size(); idx++) {
                Class javaContainer = registeredJavaContainers.get(idx);
                CppBean cppBean = new CppBean(javaContainer);
                if (cppBean.isSuccesful()) {
                    registeredJavaContainers.remove(idx);
                    registeredCppContainers.add(cppBean);
                    containerHeaderFiles.add(cppBean.getCppClassName() + ".h");
                    if (DEBUGGING_MODE) {
                        System.out.println(cppBean.getCppFilePath() + " : ");
                        System.out.println(cppBean.getCppImplementation());
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
        for (CppBean cppBean : registeredCppContainers) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(cppBean.getCppFilePath());
                writer.write(cppBean.getCppImplementation());

                writer.close();

                writer = new PrintWriter(cppBean.getHeaderFilePath());
                writer.write(cppBean.getHeaderImplementation());
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
            String javahCommand = String.format(CppSyntax.JAVAH_SECTION, classpath,
                    MetadataHandler.getHandler().getNativePath(),
                    jniFunctionClass.getName());
            SparkJni.runProcess(javahCommand);
        }
    }

    public CppBean getContainerByJavaClass(Class javaClass) {
        return getContainerByClassName(javaClass.getName());
    }

    public CppBean getContainerByClassName(String className) {
        for (CppBean cppBean : registeredCppContainers)
            if (cppBean.getJavaClass().getName().equals(className))
                return cppBean;
        return null;
    }

    public ArrayList<CppBean> getNativeMethodParams(String methodName) {
        ArrayList<CppBean> cppParameters = new ArrayList<>();
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
        for (CppBean cppBean : registeredCppContainers)
            if (cppBean.getCppClassName().equals(simpleTypeName))
                return true;
        return false;
    }

    public void registerNativePrototype(String line, String methodName){
        jniHeaderFunctionPrototypes.put(line, getNativeMethodParams(methodName));
    }

    public Class getJavaClassByName(String fullyQualifiedClassName){
        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new HardSparkJniException(String.format(Messages.ERR_CLASS_NOT_FOUND, fullyQualifiedClassName));
    }

    public void reset() {
        if(registeredJavaContainers != null)
            registeredJavaContainers.clear();
        if(registeredJniFunctions != null)
            registeredJniFunctions.clear();
    }

    public ArrayList<Class> getRegisteredJavaContainers() {
        return registeredJavaContainers;
    }

    public ArrayList<Class> getRegisteredJniFunctions() {
        return registeredJniFunctions;
    }

    public ArrayList<CppBean> getRegisteredCppContainers() {
        return registeredCppContainers;
    }

    public ArrayList<String> getJniHeaderFiles() {
        return jniHeaderFiles;
    }

    public ArrayList<String> getContainerHeaderFiles() {
        return containerHeaderFiles;
    }

    public boolean isDEBUGGING_MODE() {
        return DEBUGGING_MODE;
    }

    public void setDEBUGGING_MODE(boolean DEBUGGING_MODE) {
        this.DEBUGGING_MODE = DEBUGGING_MODE;
    }
}
