package sparkjni.jniLink.linkHandlers;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import org.immutables.value.Value;
import sparkjni.dataLink.CppBean;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.TypeMapper;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;
import sparkjni.utils.cpp.fields.CppField;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
public abstract class UserNativeFunction {
    public abstract FunctionSignatureMapper functionSignatureMapper();

    private Optional<String> functionBodyCodeInsertion = Optional.absent();

    String generateUserFunctionPrototype() {
        FunctionSignatureMapper functionSignatureMapper = functionSignatureMapper();
        String prototypeArgumentList = generatePrototypeArgumentListDefinition(functionSignatureMapper);
        return String.format(CppSyntax.FUNCTION_PROTOTYPE_STR.substring(1),
                CppSyntax.NO_ADDITIONAL_INDENTATION, functionSignatureMapper.returnTypeMapper().cppType().getCppClassName() + "*",
                functionSignatureMapper.functionNameMapper().javaName(), prototypeArgumentList);
    }

    private String generatePrototypeArgumentListDefinition(FunctionSignatureMapper functionSignatureMapper) {
        StringBuilder stringBuilder = new StringBuilder();
        List<TypeMapper> typeMapperList = functionSignatureMapper.parameterList();
        int ctr = 0;
        for (TypeMapper typeMapper : typeMapperList) {
            stringBuilder.append(String.format("%s *%s, ",
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

    String generateUserFunctionImplementation() {
        String methodPrototype = generateUserFunctionPrototype();
        // Remove semicolon and newline
        methodPrototype = methodPrototype.substring(0, methodPrototype.length() - 2);
        return String.format("%s {\n%s}\n", methodPrototype, generateDefaultFunctionContent());
    }

    private String generateDefaultFunctionContent() {
        return functionBodyCodeInsertion
                .transform(GET_INSERTION_BODY_FUNCTION)
                .or(GENERATE_DEFAULT_BODY_FUNCTION);
    }

    public void setFunctionBodyCodeInsertion(Optional<String> functionBodyCodeInsertion) {
        this.functionBodyCodeInsertion = functionBodyCodeInsertion;
    }

    private final Supplier<String> GENERATE_DEFAULT_BODY_FUNCTION = new Supplier<String>() {
        @Override
        public String get() {
            CppBean returnCppBean = functionSignatureMapper().returnTypeMapper().cppType();
            String returnTypeVariableName = JniUtils.generateCppVariableName(returnCppBean, "returned", 0);
            StringBuilder argsListBuilder = new StringBuilder();
            for (CppField cppField : returnCppBean.getCppFields()) {
                if (cppField.isArray() || cppField.isPrimitive()) {
                    argsListBuilder.append(cppField.getDefaultInitialization());
                    argsListBuilder.append(", ");
                }
            }
            argsListBuilder.append(JniUtils.generateClassNameVariableName(returnCppBean, null));
            argsListBuilder.append(", jniEnv");
            String initialization = String.format("\t%s *%s = new %s(%s);\n",
                    returnCppBean.getCppClassName(), returnTypeVariableName,
                    returnCppBean.getCppClassName(), argsListBuilder.toString());
            String returnStatement = String.format("\treturn %s;\n", returnTypeVariableName);
            return initialization + returnStatement;
        }
    };

    private final Function<String, String> GET_INSERTION_BODY_FUNCTION = new Function<String, String>() {
        @Nullable
        @Override
        public String apply(@Nullable String s) {
            return s;
        }
    };
}
