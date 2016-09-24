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

package org.tudelft.ewi.ceng.sparkjni.javaLink;

import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

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

    public boolean writeKernelFile(){
        if(jniHeaderFunctionPrototypes.isEmpty())
            throw new IllegalArgumentException(JniUtils.ERR_NO_JNI_PROTOTYPES_FOUND_IN_USER_DIR);

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
