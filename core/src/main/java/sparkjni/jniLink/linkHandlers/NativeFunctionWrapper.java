package sparkjni.jniLink.linkHandlers;

import sparkjni.dataLink.CppBean;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.TypeMapper;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;
import org.immutables.value.Value;

import java.util.HashSet;
import java.util.List;

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

    private String generateConstructorCallerArgsSection(CppBean cppBean, int variableIndex) {
        return String.format("%s, %s, jniEnv", JniUtils.getClassDefObjectVariableName(cppBean), generateJniObjectName(cppBean, variableIndex));
    }

    private String generateJniObjectName(CppBean cppBean, int variableIndex) {
        return String.format(CppSyntax.JNI_OBJECT_NAME_STR, cppBean.getCppClassName().toLowerCase(), variableIndex);
    }

    private String generateInitializationStatementForJniPrototype(FunctionSignatureMapper functionSignatureMapper, int variableIndex) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(variableIndex).cppType();
        String cppVariableName = JniUtils.generateCppVariableName(cppBean, "", variableIndex);
        String arguments = generateConstructorCallerArgsSection(cppBean, variableIndex);
        return generateInitializationStatement(cppBean.getCppClassName(), cppVariableName, arguments);
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
        sb.append(generateMethodCallReturnStatement(functionSignatureMapper));
        return sb.toString();
    }

    private String generateInitializationStatement(String type, String varName, String argsSection) {
        if (argsSection == null || argsSection.isEmpty())
            return String.format("\t%s %s;\n", type, varName);
        else
            return String.format("\t%s %s(%s);\n", type, varName, argsSection);
    }

    private String generateClassObjectInitializationFor(FunctionSignatureMapper functionSignatureMapper, int idx) {
        CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
        String candidateClassObjectName = JniUtils.generateClassNameVariableName(cppBean, jClassObjectsSet);
        if(candidateClassObjectName.isEmpty())
            return "";
        else {
            String instanceOfThyClassName = generateJniObjectName(cppBean, idx);
            return String.format("\tjclass %s = jniEnv->GetObjectClass(%s);\n", candidateClassObjectName, instanceOfThyClassName);
        }
    }

    @Deprecated
    private String generateReturnStatement(FunctionSignatureMapper functionSignatureMapper) {
        TypeMapper returnTypeMapper = functionSignatureMapper.returnTypeMapper();
        CppBean returnedBean = returnTypeMapper.cppType();
        String returnedObjectName = JniUtils.generateCppVariableName(returnedBean, "returned", 0);
        String varInit = generateInitializationStatement(returnedBean.getCppClassName(), returnedObjectName,
                generateNativeConstructorCallerArgsSection(returnedBean));

        return generateIfNecessaryFindClassStatementFor(returnTypeMapper)
                + varInit + String.format("\treturn %s.getJavaObject();\n", returnedObjectName);
    }

    private String generateMethodCallReturnStatement(FunctionSignatureMapper functionSignatureMapper){
        TypeMapper returnTypeMapper = functionSignatureMapper.returnTypeMapper();
        CppBean returnedBean = returnTypeMapper.cppType();
//        String returnedObjectName = JniUtils.generateCppVariableName(returnedBean, "returned", 0);
        StringBuilder callerArgs = new StringBuilder();
        for (int idx = 0; idx < functionSignatureMapper.parameterList().size(); idx++) {
            CppBean cppBean = functionSignatureMapper.parameterList().get(idx).cppType();
            callerArgs.append(String.format("%s, ", JniUtils.generateCppVariableName(cppBean, null, idx)));
        }
        callerArgs.append(JniUtils.getClassDefObjectVariableName(returnedBean));
        callerArgs.append(", jniEnv");

//        String returnedObjectCreation = String.format("\t%s %s = %s(%s);\n",
//                returnedBean.getCppClassName(), returnedObjectName,
//                functionSignatureMapper.functionNameMapper().cppName(),
//                callerArgs.toString());
        String functionCall = String.format("%s(%s)", functionSignatureMapper.functionNameMapper().cppName(),
                callerArgs.toString());
        return String.format("\treturn %s.getJavaObject();\n", functionCall);
    }

    public String generateIfNecessaryFindClassStatementFor(TypeMapper typeMapper){
        String classVariableName = JniUtils.generateClassNameVariableName(typeMapper.cppType(), jClassObjectsSet);
        if(classVariableName.isEmpty())
            return "";
        else {
            String jniClassPath = JniUtils.generateJniPathForClass(typeMapper.javaType());
            return String.format("\tjclass %s = jniEnv->FindClass(\"%s\");\n", classVariableName, jniClassPath);
        }
    }

    private String generateNativeConstructorCallerArgsSection(CppBean returnedBean) {
        return "";
    }
}
