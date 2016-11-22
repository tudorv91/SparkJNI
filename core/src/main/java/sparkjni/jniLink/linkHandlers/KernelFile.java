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

import sparkjni.utils.JniUtils;
import sparkjni.utils.MetadataHandler;
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
