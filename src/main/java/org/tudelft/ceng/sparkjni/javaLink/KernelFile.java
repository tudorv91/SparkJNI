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

package org.tudelft.ceng.sparkjni.javaLink;

import org.tudelft.ceng.sparkjni.utils.CppBean;
import org.tudelft.ceng.sparkjni.utils.JniUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KernelFile {
    public static final String NATIVE_METHOD_IMPL_STR = "JNIEXPORT %s JNICALL %s(%s){\n%s}";
    public static final String JNI_CLASSNAME_STR = "%s_jClass";
    public static final String JNI_OBJECT_NAME_STR = "%s_jObject%d";

    private String targetCppKernelFileName;
    private ArrayList<String> jniHeaderFiles;
    private ArrayList<String> containerHeaderFiles;
    private List<JniHeaderHandler> jniHeaderHandlers;
    private HashSet<String> jClassObjectsSet = new HashSet<>();

    public KernelFile(String targetCppKernelFileName, ArrayList<String> jniHeaderFiles, ArrayList<String> containerHeaderFiles,
                      List<JniHeaderHandler> jniHeaderHandlers) {
        this.targetCppKernelFileName = targetCppKernelFileName;
        this.jniHeaderFiles = jniHeaderFiles;
        this.containerHeaderFiles = containerHeaderFiles;
        this.jniHeaderHandlers = jniHeaderHandlers;
    }

    public boolean writeKernelFile() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> headers = new ArrayList<>();

        headers.addAll(jniHeaderFiles);
        headers.addAll(containerHeaderFiles);

        sb.append(JniUtils.generateIncludeStatements(false, headers.toArray(new String[]{})));
        sb.append("\n");
        sb.append(generateImplementationBody());

        return writeFile(sb.toString());
    }

    private String generateImplementationBody() {
        StringBuilder sb = new StringBuilder();
        for (JniHeaderHandler jniHeaderHandler : jniHeaderHandlers) {
            String jniHeaderImplementation = String.format("%s\n\n", generateJniHeaderImplementationBody(jniHeaderHandler));
            sb.append(jniHeaderImplementation);
        }
        return sb.toString();
    }

    private String generateJniHeaderImplementationBody(JniHeaderHandler jniHeaderHandler) {
        StringBuilder sb = new StringBuilder();
        for (JniFunctionPrototype jniFunctionPrototype : jniHeaderHandler.getJniFunctions()) {
            sb.append(generateNativeMethodImplementation(jniFunctionPrototype));
        }
        return sb.toString();
    }

    private String generateNativeMethodImplementation(JniFunctionPrototype jniFunctionPrototype) {
        jClassObjectsSet.clear();
        StringBuilder sb = new StringBuilder();
        String secondArg = jniFunctionPrototype.isStatic() ? "jclass callerStaticClass, " : "jobject callerObject";
        sb.append(String.format("JNIEnv *jniEnv, %s, ", secondArg));
        String argumentString = generateJNIArgumentPrototypeDecl(jniFunctionPrototype.getParameterList());

        String aux = sb.append(argumentString).toString();
        String argumentSection = aux.substring(0, aux.length() - 2);
        return String.format(NATIVE_METHOD_IMPL_STR, "jobject", jniFunctionPrototype.getJniFuncName(),
                argumentSection, generateNativeMethodBody(jniFunctionPrototype));
    }

    private String generateNativeMethodBody(JniFunctionPrototype jniFunctionPrototype) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < jniFunctionPrototype.getParameterList().size(); idx++) {
            sb.append(generateClassObjectInitializationFor(jniFunctionPrototype, idx));
            sb.append(generateInitializationStatement(jniFunctionPrototype, idx));
        }
        sb.append(generateReturnStatement(jniFunctionPrototype.getCppReturnType()));
        return sb.toString();
    }

    private String generateClassObjectInitializationFor(JniFunctionPrototype jniFunctionPrototype, int idx) {
        CppBean cppBean = jniFunctionPrototype.getParameterList().get(idx);
        String candidateClassObjectName = generateClassNameVariableName(cppBean);
        String instanceOfThyClassName = generateJniObjectName(cppBean, idx);
        if(jClassObjectsSet.contains(candidateClassObjectName))
            return "";
        else {
            jClassObjectsSet.add(candidateClassObjectName);
            return String.format("\tjclass %s = jniEnv->GetObjectClass(%s);\n", candidateClassObjectName, instanceOfThyClassName);
        }
    }

    private String generateReturnStatement(CppBean cppReturnType) {
        return "\t// add RETURN OBJECT.getJavaObject()\n";
    }

    private String generateInitializationStatement(JniFunctionPrototype jniFunctionPrototype, int variableIndex) {
        CppBean cppBean = jniFunctionPrototype.getParameterList().get(variableIndex);
        String cppVariableName = String.format("%s%d", cppBean.getCppClassName().toLowerCase(), variableIndex);
        String arguments = generateConstructorCallerArgsSection(cppBean, variableIndex);
        return String.format("\t%s %s(%s);\n", cppBean.getCppClassName(), cppVariableName, arguments);
    }

    private String generateConstructorCallerArgsSection(CppBean cppBean, int variableIndex) {
        return String.format("%s, %s, jniEnv", generateClassNameVariableName(cppBean), generateJniObjectName(cppBean, variableIndex));
    }

    private String generateJNIArgumentPrototypeDecl(ArrayList<CppBean> parameterList) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < parameterList.size(); idx++) {
            CppBean cppBean = parameterList.get(idx);
            sb.append(String.format("%s %s, ", "jobject", generateJniObjectName(cppBean, idx)));
        }
        return sb.toString();
    }

    private String generateClassNameVariableName(CppBean cppBean) {
        return String.format(JNI_CLASSNAME_STR, cppBean.getCppClassName().toLowerCase());
    }

    private String generateJniObjectName(CppBean cppBean, int variableIndex){
        return String.format(JNI_OBJECT_NAME_STR, cppBean.getCppClassName().toLowerCase(), variableIndex);
    }

    private boolean writeFile(String content) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetCppKernelFileName);
            writer.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
        return true;
    }
}
