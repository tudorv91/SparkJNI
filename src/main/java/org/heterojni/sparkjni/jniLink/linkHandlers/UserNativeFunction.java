package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.Optional;
import org.heterojni.sparkjni.dataLink.CppBean;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkContainers.TypeMapper;
import org.heterojni.sparkjni.utils.CppSyntax;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.cpp.fields.CppField;
import org.immutables.value.Value;

import java.util.HashSet;
import java.util.List;

@Value.Immutable
public abstract class UserNativeFunction {
    public abstract FunctionSignatureMapper functionSignatureMapper();

    public Optional<String> functionBodyCodeInsertion;

    private HashSet<String> objectNameHashSet = new HashSet<>();

    public String generateUserFunctionPrototype(){
        FunctionSignatureMapper functionSignatureMapper = functionSignatureMapper();
        String prototypeArgumentList = generatePrototypeArgumentListDefinition(functionSignatureMapper);
        return String.format(CppSyntax.FUNCTION_PROTOTYPE_STR.substring(1),
                CppSyntax.NO_ADDITIONAL_INDENTATION, functionSignatureMapper.returnTypeMapper().cppType().getCppClassName()+"&",
                functionSignatureMapper.functionNameMapper().javaName(), prototypeArgumentList);
    }
    private String generatePrototypeArgumentListDefinition(FunctionSignatureMapper functionSignatureMapper){
        StringBuilder stringBuilder = new StringBuilder();
        List<TypeMapper> typeMapperList = functionSignatureMapper.parameterList();
        int ctr = 0;
        for(TypeMapper typeMapper: typeMapperList){
            stringBuilder.append(String.format("%s& %s, ",
                    typeMapper.cppType().getCppClassName(),
                    JniUtils.generateCppVariableName(typeMapper.cppType(), null, ctr++)
            ));
        }

        CppBean returnedContainerType = functionSignatureMapper.returnTypeMapper().cppType();
        stringBuilder.append(String.format(" jclass %s, %s %s",
                JniUtils.generateClassNameVariableName(returnedContainerType, null),
                CppSyntax.JNIENV_PTR, "jniEnv"));
        return stringBuilder.toString();
    }

    public String generateUserFunctionImplementation(){
        String methodPrototype = generateUserFunctionPrototype();
        // Remove semicolon and newline
        methodPrototype = methodPrototype.substring(0, methodPrototype.length() - 2);
        return String.format("%s {\n%s}\n", methodPrototype, generateUserHelperContent());
    }

    private String generateUserHelperContent() {
        FunctionSignatureMapper functionSignatureMapper = functionSignatureMapper();
        if(functionBodyCodeInsertion != null && functionBodyCodeInsertion.isPresent())
            return functionBodyCodeInsertion.get();
        else {
            CppBean returnCppBean = functionSignatureMapper.returnTypeMapper().cppType();
            String returnTypeVariableName = JniUtils.generateCppVariableName(returnCppBean, "returned", 0);
            StringBuilder argsListBuilder = new StringBuilder();
            for(CppField cppField: returnCppBean.getCppFields()){
                if(cppField.isArray() || cppField.isPrimitive())
                    argsListBuilder.append(cppField.getDefaultInitialization() + ", ");
            }
            argsListBuilder.append(JniUtils.generateClassNameVariableName(returnCppBean, null) + ", jniEnv");
            String initialization = String.format("\t%s %s(%s);\n",
                    returnCppBean.getCppClassName(), returnTypeVariableName, argsListBuilder.toString());
            String returnStatement = String.format(String.format("\treturn %s;\n", returnTypeVariableName));
            return initialization + returnStatement;
        }
    }

    public Optional<String> getFunctionBodyCodeInsertion() {
        return functionBodyCodeInsertion;
    }

    public void setFunctionBodyCodeInsertion(Optional<String> functionBodyCodeInsertion) {
        this.functionBodyCodeInsertion = functionBodyCodeInsertion;
    }
}
