package org.heterojni.sparkjni.jniLink.linkHandlers;

import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkContainers.JniHeader;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.heterojni.sparkjni.jniLink.linkContainers.TypeMapper;
import org.heterojni.sparkjni.utils.CppSyntax;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.MetadataHandler;
import org.heterojni.sparkjni.utils.SparkJni;
import org.immutables.value.Value;

@Value.Immutable
public abstract class KernelFile {
    public abstract JniRootContainer jniRootContainer();
    public abstract String kernelWrapperFileName();

    private String targetKernelFilename;
    private String nativePath;
    private String fileContent;

    private void handleDependencies(){
        nativePath = MetadataHandler.getHandler().getNativePath();
        targetKernelFilename = JniUtils.generateDefaultKernelFileName(MetadataHandler.getHandler().getAppName(), nativePath);
        generate();
    }

    private void generate(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JniUtils.generateIncludeStatements(false, new String[]{kernelWrapperFileName()}));
        for(JniHeader jniHeader: jniRootContainer().jniHeaders()){
            stringBuilder.append(String.format("%s\n%s\n\n",
                    generateCommentsInfoFor(jniHeader), generateHeaderWrapperImplFor(jniHeader)));
        }
        fileContent = stringBuilder.toString();
    }

    private String generateHeaderWrapperImplFor(JniHeader jniHeader) {
        StringBuilder stringBuilder = new StringBuilder();
        for(FunctionSignatureMapper functionSignatureMapper: jniHeader.jniFunctions()){
            generateFunctionImplFor(functionSignatureMapper);
        }
        return stringBuilder.toString();
    }

    private String generateFunctionImplFor(FunctionSignatureMapper functionSignatureMapper) {
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 0;
        for(TypeMapper typeMapper: functionSignatureMapper.parameterList()){
            String argObjectName = typeMapper.cppType().getCppClassName().toLowerCase();
            stringBuilder.append(String.format("%s %s%d, ",
                    typeMapper.cppType().getCppClassName(),
                    argObjectName, counter++));
        }
        if(stringBuilder.length() > 2)
            stringBuilder.substring(0, stringBuilder.length() - 2);

        return String.format("%s %s(%s){\n%s}\n",
                functionSignatureMapper.returnTypeMapper().cppType(),
                functionSignatureMapper.functionNameMapper().cppName(),
                stringBuilder.toString(),
                generateReturnStatements(functionSignatureMapper));
    }

    private String generateReturnStatements(FunctionSignatureMapper functionSignatureMapper) {
        String cppObjectName = functionSignatureMapper.returnTypeMapper().cppType().getCppClassName().toLowerCase();
        String returnObjectDefinitionStatement = String.format("\t%s %s;\n",
                functionSignatureMapper.returnTypeMapper().cppType().getCppClassName(),
                cppObjectName, "");
        String returnStatement = String.format("\treturn %s;\n", cppObjectName);
        return returnObjectDefinitionStatement + returnStatement;
    }

    private String generateCommentsInfoFor(JniHeader jniHeader) {
        return "";
    }

    public void writeKernelFile(){
        handleDependencies();
        JniUtils.writeFile(fileContent, targetKernelFilename);
    }
}
