package org.heterojni.sparkjni.jniLink.linkHandlers;


import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.MetadataHandler;
import org.heterojni.sparkjni.utils.SparkJni;

public class KernelFileGenerator {
    private FunctionSignatureMapper functionSignatureMapper;
    private String targetKernelFilename;
    private String nativePath;
    private String fileContent;

    public KernelFileGenerator(FunctionSignatureMapper functionSignatureMapper) {
        this.functionSignatureMapper = functionSignatureMapper;
        handleDependencies();
//        generate();
        writeKernelFile();
    }

    private void handleDependencies(){
        nativePath = String.format("%s/%s.cpp", MetadataHandler.getHandler().getNativePath());
    }

//    private String generate(){
//        StringBuilder stringBuilder = new StringBuilder();
//        for(TypeMapper typeMapper: functionSignatureMapper.parameterList()){
//            for(FunctionSignatureMapperProvider jniFunctionPrototype: jniHeaderHandler.getJniFunctions()) {
//                String functionImplementation = String.format("%s\n\n", generateFunctionImplementation(jniHeaderHandler, jniFunctionPrototype));
//                stringBuilder.append(functionImplementation);
//            }
//        }
//    }

//    private String generateFunctionImplementation(JniHeaderHandler jniHeaderHandler, FunctionSignatureMapperProvider jniFunctionPrototype) {
//        StringBuilder stringBuilder = new StringBuilder();
//        generateFunctionHeader(jniFunctionPrototype);
//        String returnType = generateReturnType(jniFunctionPrototype);
//    }

    private void generateFunctionHeader(FunctionSignatureMapperProvider functionSignatureMapperProvider) {
    }

    private void writeKernelFile(){
        if(SparkJni.isDoForceOverwriteKernelFiles())
            JniUtils.writeFile(fileContent, targetKernelFilename);
    }
}
