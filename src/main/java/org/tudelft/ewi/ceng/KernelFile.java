package org.tudelft.ewi.ceng;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by root on 8/6/16.
 */
public class KernelFile {
    private ArrayList<CppClass> registeredContainers;
    private String implementation;
    private String nativePath;
    private String targetCppKernelFileName;
    private ArrayList<String> jniHeaderFiles;
    private TreeMap<String, ArrayList<CppClass>> jniHeaderFunctionPrototypes;
    private ArrayList<String> containerHeaderFiles;

    public KernelFile(ArrayList<CppClass> usedClasses, String nativeLibPath, String targetCppKernelFileName,
                      ArrayList<String> jniHeaderFiles, TreeMap<String, ArrayList<CppClass>> jniHeaderFunctionPrototypes,
                      ArrayList<String> containerHeaderFiles){
        this.registeredContainers = usedClasses;
        this.nativePath = nativeLibPath;
        this.targetCppKernelFileName = targetCppKernelFileName;
        this.jniHeaderFunctionPrototypes = jniHeaderFunctionPrototypes;
        this.jniHeaderFiles = jniHeaderFiles;
        this.containerHeaderFiles = containerHeaderFiles;
    }

    public boolean writeTemplateFile(){
        if(jniHeaderFunctionPrototypes.isEmpty())
            throw new IllegalArgumentException("No JNI prototypes found in user dir..");

        StringBuilder sb = new StringBuilder();

        ArrayList<String> headers = new ArrayList<>();
        headers.addAll(jniHeaderFiles);
        headers.addAll(containerHeaderFiles);

        sb.append(JniUtils.generateIncludeStatements(false, headers.toArray(new String[]{})));
        for(Map.Entry<String, ArrayList<CppClass>> funcPrototypesClassEntry: jniHeaderFunctionPrototypes.entrySet())
                sb.append(generateJniFunctionImpl(funcPrototypesClassEntry)+"\n");

        PrintWriter writer = null;
        try{
            writer = new PrintWriter(targetCppKernelFileName);
            writer.write(sb.toString());
        } catch(IOException ex){
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
        return true;
    }

    public String generateJniFunctionImpl(Map.Entry<String, ArrayList<CppClass>> funcPrototypeClassName) {
        return String.format(JniUtils.JNI_CONSTRUCTOR_IMPL_STR, funcPrototypeClassName.getKey(),
                generateJniFunctionBody(funcPrototypeClassName.getValue()));
    }

    public String generateJniFunctionBody(ArrayList<CppClass> containers) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for(CppClass incomingObjects: containers)
            sb.append(String.format(JniUtils.JNI_FUNCTION_BODY_STMT_STR,
                    counter, incomingObjects.getCppClassName(), counter++));
        return String.format(JniUtils.JNI_FUNCTION_BODY_STR, sb.toString());
    }
}
