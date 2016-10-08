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

package org.heterojni.sparkjni.jniLink.linkHandlers;

import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkContainers.TypeMapper;
import org.heterojni.sparkjni.utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KernelFileWrapperHeader {
    public static final String NATIVE_METHOD_IMPL_STR = "JNIEXPORT %s JNICALL %s(%s){\n%s}";
    public static final String JNI_CLASSNAME_STR = "%s_jClass";
    public static final String JNI_OBJECT_NAME_STR = "%s_jObject%d";

    private String targetCppKernelFileName;

    private ArrayList<String> jniHeaderFiles;
    private ArrayList<String> containerHeaderFiles;

    private List<JniHeaderHandler> jniHeaderHandlers;

    private HashSet<String> jClassObjectsSet = new HashSet<>();
    public KernelFileWrapperHeader(String targetCppKernelFileName, ArrayList<String> jniHeaderFiles,
                                   ArrayList<String> containerHeaderFiles,
                                   List<JniHeaderHandler> jniHeaderHandlers) {
        this.targetCppKernelFileName = targetCppKernelFileName;
        this.jniHeaderFiles = jniHeaderFiles;
        this.containerHeaderFiles = containerHeaderFiles;
        this.jniHeaderHandlers = jniHeaderHandlers;
    }
    public boolean writeKernelWrapperFile() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> headers = new ArrayList<>();

        headers.addAll(jniHeaderFiles);
        headers.addAll(containerHeaderFiles);

        sb.append(JniUtils.generateIncludeStatements(false, headers.toArray(new String[]{})));
        sb.append("\n");
        sb.append(generateImplementationBody());

        String headerWrapperFileName = targetCppKernelFileName.replace(".cpp", ".h");
        if(SparkJni.isDoForceOverwriteKernelFiles())
            JniUtils.writeFile(wrapInHeader(sb.toString()), headerWrapperFileName);
        return true;
    }

    private String wrapInHeader(String s) {
        String macroName = MetadataHandler.getHandler().getAppName().toUpperCase() + "_KERNELWRAPPER";
        return String.format(CppSyntax.SIMPLE_HEADER_FILE_STR, macroName, macroName, s);
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
        for (FunctionSignatureMapper functionSignatureMapper: jniHeaderHandler.getJniFunctions()) {
            sb.append(generateNativeMethodImplementation(functionSignatureMapper));
        }
        return sb.toString();
    }

    private String generateNativeMethodImplementation(FunctionSignatureMapper functionSignatureMapper) {
        jClassObjectsSet.clear();
        StringBuilder sb = new StringBuilder();

        String secondArg = functionSignatureMapper.staticMethod() ? "jclass callerStaticClass, " : "jobject callerObject";
        sb.append(String.format("JNIEnv *jniEnv, %s, ", secondArg));
        String argumentString = generateJNIArgumentPrototypeDecl(functionSignatureMapper.parameterList());

        String aux = sb.append(argumentString).toString();
        String argumentSection = aux.substring(0, aux.length() - 2);
        return String.format(NATIVE_METHOD_IMPL_STR, "jobject", functionSignatureMapper.functionNameMapper().jniName(),
                argumentSection, generateNativeMethodBody(functionSignatureMapper));
    }

    private String generateNativeMethodBody(FunctionSignatureMapper functionSignatureMapper) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < functionSignatureMapper.parameterList().size(); idx++) {
            sb.append(generateClassObjectInitializationFor(functionSignatureMapper, idx));
            sb.append(generateInitializationStatementForJniPrototype(functionSignatureMapper, idx));
        }
        sb.append(generateReturnStatement(functionSignatureMapper));
        return sb.toString();
    }

    private String generateClassObjectInitializationFor(FunctionSignatureMapper functionSignatureMapper, int idx) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
        String candidateClassObjectName = generateClassNameVariableName(cppBean);
        String instanceOfThyClassName = generateJniObjectName(cppBean, idx);
        if(jClassObjectsSet.contains(candidateClassObjectName))
            return "";
        else {
            jClassObjectsSet.add(candidateClassObjectName);
            return String.format("\tjclass %s = jniEnv->GetObjectClass(%s);\n", candidateClassObjectName, instanceOfThyClassName);
        }
    }

    private String generateReturnStatement(FunctionSignatureMapper functionSignatureMapper) {
            CppBean returnedBean = functionSignatureMapper.returnTypeMapper().cppType();
            String returnedObjectName = generateCppVariableName(returnedBean, "returned", 0);
            String varInit = generateInitializationStatement(returnedBean.getCppClassName(), returnedObjectName,
                    generateNativeConstructorCallerArgsSection(returnedBean));
            return varInit + String.format("\treturn %s.getJavaObject();\n", returnedObjectName);
    }

    private String generateNativeConstructorCallerArgsSection(CppBean returnedBean) {
        return "";
    }

    private String generateInitializationStatementForJniPrototype(FunctionSignatureMapper functionSignatureMapper, int variableIndex) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(variableIndex).cppType();
        String cppVariableName = generateCppVariableName(cppBean, "", variableIndex);
        String arguments = generateConstructorCallerArgsSection(cppBean, variableIndex);
        return generateInitializationStatement(cppBean.getCppClassName(), cppVariableName, arguments);
    }

    private String generateCppVariableName(CppBean cppBean, String prefix, int idx){
        String idStr = String.format("%s%d", cppBean.getCppClassName().toLowerCase(), idx);
        prefix = prefix == null ? "" : prefix;
        return prefix.isEmpty() ? idStr : prefix + JniUtils.firstLetterCaps(idStr);
    }

    private String generateInitializationStatement(String type, String varName, String argsSection){
        if(argsSection == null || argsSection.isEmpty())
            return String.format("\t%s %s;\n", type, varName);
        else
            return String.format("\t%s %s(%s);\n", type, varName, argsSection);
    }

    private String generateConstructorCallerArgsSection(CppBean cppBean, int variableIndex) {
        return String.format("%s, %s, jniEnv", generateClassNameVariableName(cppBean), generateJniObjectName(cppBean, variableIndex));
    }

    private String generateJNIArgumentPrototypeDecl(List<TypeMapper> parameterList) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < parameterList.size(); idx++) {
            CppBean cppBean = parameterList.get(idx).cppType();
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

    public List<JniHeaderHandler> getJniHeaderHandlers() {
        return jniHeaderHandlers;
    }

}
