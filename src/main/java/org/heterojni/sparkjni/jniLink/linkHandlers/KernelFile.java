package org.heterojni.sparkjni.jniLink.linkHandlers;

import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.MetadataHandler;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class KernelFile {
    public abstract String kernelWrapperFileName();
    public abstract List<UserNativeFunction> userNativeFunctions();
    public abstract String nativePath();

    private String targetKernelFilename;
    private String fileContent;

    private void handleDependencies(){
        targetKernelFilename = JniUtils.generateDefaultKernelFileName(MetadataHandler.getHandler().getAppName(), nativePath());
        generate();
    }

    private void generate(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JniUtils.generateIncludeStatements(false, new String[]{kernelWrapperFileName()}));

        for(UserNativeFunction userNativeFunction: userNativeFunctions()){
            stringBuilder.append(userNativeFunction.generateUserFunctionImplementation());
            stringBuilder.append("\n");
        }
        fileContent = stringBuilder.toString();
    }

    public void writeKernelFile(){
        handleDependencies();
        JniUtils.writeFile(fileContent, targetKernelFilename);
    }
}
