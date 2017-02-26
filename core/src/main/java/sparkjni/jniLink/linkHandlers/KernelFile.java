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
import sparkjni.utils.JniUtils;
import sparkjni.utils.MetadataHandler;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

import static sparkjni.utils.AppInjector.injectMembers;

@Value.Immutable
public abstract class KernelFile {
    @Inject
    private MetadataHandler metadataHandler;

    public abstract String kernelWrapperFileName();
    public abstract List<UserNativeFunction> userNativeFunctions();
    public abstract String nativePath();

    private String targetKernelFilename;
    private String fileContent;

    private void handleDependencies(){
        targetKernelFilename = JniUtils.generateDefaultKernelFileName(metadataHandler.getAppName(), nativePath());
        generate();
    }

    private void generate(){
        StringBuilder stringBuilder = new StringBuilder();
        String strippedFileName = new File(kernelWrapperFileName()).toPath().getFileName().toString();
        stringBuilder.append(JniUtils.generateIncludeStatements(false, new String[]{strippedFileName}));

        for(UserNativeFunction userNativeFunction: userNativeFunctions()){
            stringBuilder.append(userNativeFunction.generateUserFunctionImplementation());
            stringBuilder.append("\n");
        }
        fileContent = stringBuilder.toString();
    }

    public void writeKernelFile(boolean overWriteKernelFile){
        injectMembers(this);
        handleDependencies();
        JniUtils.writeFile(fileContent, targetKernelFilename, overWriteKernelFile);
    }
}
