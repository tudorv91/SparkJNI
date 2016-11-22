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
package sparkjni.jniLink.linkHandlers;

import org.immutables.value.Value;
import sparkjni.dataLink.CppBean;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.TypeMapper;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;

import java.util.HashSet;
import java.util.List;

import static sparkjni.utils.JniUtils.PASS_BY_VALUE;

@Value.Immutable
public abstract class NativeFunctionWrapper {
    private HashSet<String> jClassObjectsSet = new HashSet<>();

    public abstract FunctionSignatureMapper functionSignatureMapper();

    public String generateNativeMethodImplementation() {
        FunctionSignatureMapper functionSignatureMapper = functionSignatureMapper();
        StringBuilder sb = new StringBuilder();
        String secondArg = functionSignatureMapper.staticMethod() ? "jclass callerStaticClass, " : "jobject callerObject";
        sb.append(String.format("JNIEnv *jniEnv, %s, ", secondArg));
        String argumentString = generateJNIArgumentPrototypeDecl(functionSignatureMapper.parameterList());

        String aux = sb.append(argumentString).toString();
        String argumentSection = aux.substring(0, aux.length() - 2);
        return String.format(CppSyntax.NATIVE_METHOD_IMPL_STR, "jobject", functionSignatureMapper.functionNameMapper().jniName(),
                argumentSection, generateNativeMethodBody(functionSignatureMapper));
    }

    private String generateInitializationStatementForJniPrototype(FunctionSignatureMapper functionSignatureMapper, int variableIndex) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(variableIndex).cppType();
        String cppVariableName = JniUtils.generateCppVariableName(cppBean, "", variableIndex);
        String arguments = generateConstructorCallerArgsSection(cppBean, variableIndex);
        String args = (arguments == null || arguments.isEmpty()) ? "" : arguments;
        String retType = JniUtils.wrapInSharedPtr(cppBean.getCppClassName(), PASS_BY_VALUE);
        return String.format("\t%s %s = std::make_shared<%s>(%s);\n", retType, cppVariableName, cppBean.getCppClassName(), args);
    }

    private String generateJNIArgumentPrototypeDecl(List<TypeMapper> parameterList) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < parameterList.size(); idx++) {
            CppBean cppBean = parameterList.get(idx).cppType();
            sb.append(String.format("%s %s, ", "jobject", generateJniObjectName(cppBean, idx)));
        }
        return sb.toString();
    }

    private String generateNativeMethodBody(FunctionSignatureMapper functionSignatureMapper) {
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < functionSignatureMapper.parameterList().size(); idx++) {
            sb.append(generateClassObjectInitializationFor(functionSignatureMapper, idx));
            sb.append(generateInitializationStatementForJniPrototype(functionSignatureMapper, idx));
        }
        sb.append(generateMethodCall(functionSignatureMapper));
//        sb.append(generatePtrDeleteStatements(functionSignatureMapper));
        sb.append(generateReturnStatement(functionSignatureMapper));
        return sb.toString();
    }

    private String generateReturnStatement(FunctionSignatureMapper functionSignatureMapper){
        return "\treturn " + generateReturnObjectName(functionSignatureMapper.returnTypeMapper().cppType()) + ";\n";
    }

    private String generatePtrDeleteStatements(FunctionSignatureMapper functionSignatureMapper) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int idx = 0; idx < functionSignatureMapper.parameterList().size(); idx++) {
            CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
            String cppVariableName = JniUtils.generateCppVariableName(cppBean, "", idx);
            stringBuilder.append(String.format("\tdelete %s;\n", cppVariableName));
            stringBuilder.append(String.format("\t%s = NULL;\n", cppVariableName));
        }
        String funcRetObjName = generateFunctionReturnObjectName(functionSignatureMapper.returnTypeMapper().cppType());
//        stringBuilder.append(String.format("\tif(%s != NULL)\n\t\tdelete %s;\n", funcRetObjName, funcRetObjName));
        return stringBuilder.toString();
    }

    private String generateFunctionReturnObjectName(CppBean cppBean){
        return String.format("%s_ret", cppBean.getCppClassName());
    }

    private String generateReturnObjectName(CppBean cppBean){
        return String.format("%s_retJavaObj", cppBean.getCppClassName());
    }

    private String generateClassObjectInitializationFor(FunctionSignatureMapper functionSignatureMapper, int idx) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
        String candidateClassObjectName = JniUtils.generateClassNameVariableName(cppBean, jClassObjectsSet);
        if (candidateClassObjectName.isEmpty())
            return "";
        else {
            String instanceOfThyClassName = generateJniObjectName(cppBean, idx);
            return String.format("\tjclass %s = jniEnv->GetObjectClass(%s);\n", candidateClassObjectName, instanceOfThyClassName);
        }
    }

    private String generateMethodCall(FunctionSignatureMapper functionSignatureMapper){
        TypeMapper returnTypeMapper = functionSignatureMapper.returnTypeMapper();
        CppBean returnedBean = returnTypeMapper.cppType();
        StringBuilder callerArgs = new StringBuilder();
        for (int idx = 0; idx < functionSignatureMapper.parameterList().size(); idx++) {
            CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
            callerArgs.append(String.format("%s, ", JniUtils.generateCppVariableName(cppBean, null, idx)));
        }
        callerArgs.append(JniUtils.getClassDefObjectVariableName(returnedBean));
        callerArgs.append(", jniEnv");

        CppBean cppbean = functionSignatureMapper.returnTypeMapper().cppType();
        String retObjectName = generateFunctionReturnObjectName(cppbean);
        String retType = JniUtils.wrapInSharedPtr(cppbean.getCppClassName(), PASS_BY_VALUE);
        String retObjFuncCallStmt = String.format("\t%s %s = %s(%s);\n", retType, retObjectName,
                functionSignatureMapper.functionNameMapper().cppName(), callerArgs.toString());
        String result = String.format("\t%s %s = %s->getJavaObject();\n", "jobject", generateReturnObjectName(cppbean), retObjectName);
        return retObjFuncCallStmt + result;
    }

    private String generateConstructorCallerArgsSection(CppBean cppBean, int variableIndex) {
        return String.format("%s, %s, jniEnv", JniUtils.getClassDefObjectVariableName(cppBean), generateJniObjectName(cppBean, variableIndex));
    }

    private String generateJniObjectName(CppBean cppBean, int variableIndex) {
        return String.format(CppSyntax.JNI_OBJECT_NAME_STR, cppBean.getCppClassName().toLowerCase(), variableIndex);
    }
}
