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

package sparkjni.jniLink.linkHandlers;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;
import sparkjni.utils.MetadataHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class KernelFileWrapperHeader {
    private ArrayList<String> containerHeaderFiles;
    private JniRootContainer jniRootContainer;
    private String headerWrapperFileName;

    private List<UserNativeFunction> userNativeFunctions;

    private List<NativeFunctionWrapper> nativeFunctionWrappers;
    public KernelFileWrapperHeader(ArrayList<String> containerHeaderFiles, JniRootContainer jniRootContainer) {
        this.containerHeaderFiles = containerHeaderFiles;
        this.jniRootContainer = jniRootContainer;
    }

    public boolean writeKernelWrapperFile() {
        StringBuilder sb = new StringBuilder();
        populateDependencies();

        sb.append(JniUtils.generateIncludeStatements(false, generateHeaders()));
        sb.append("\n");
        sb.append(generateUserFunctionsPrototypes());
        sb.append(generateWrappersImplementationBody());

        headerWrapperFileName = JniUtils.generateDefaultHeaderWrapperFileName(jniRootContainer.appName(),
                MetadataHandler.getHandler().getNativePath());

        JniUtils.writeFile(wrapInHeader(sb.toString()), headerWrapperFileName);
        return true;
    }

    public String[] generateHeaders(){
        ArrayList<String> headers = new ArrayList<>();
        headers.addAll(getJniHeadersPaths());
        headers.addAll(containerHeaderFiles);
        return headers.toArray(new String[]{});
    }

    private void populateDependencies() {
        userNativeFunctions = new ArrayList<>();
        nativeFunctionWrappers = new ArrayList<>();
        for(JniHeader jniHeader: jniRootContainer.jniHeaders())
            for(FunctionSignatureMapper functionSignatureMapper: jniHeader.jniFunctions()){
                userNativeFunctions.add(
                        ImmutableUserNativeFunction.builder().functionSignatureMapper(functionSignatureMapper).build()
                );
                nativeFunctionWrappers.add(
                        ImmutableNativeFunctionWrapper.builder().functionSignatureMapper(functionSignatureMapper).build()
                );
            }
    }

    private String generateUserFunctionsPrototypes() {
        StringBuilder stringBuilder = new StringBuilder();
        for(UserNativeFunction userNativeFunction: userNativeFunctions)
            stringBuilder.append(userNativeFunction.generateUserFunctionPrototype());
        return stringBuilder.toString();
    }

    private String wrapInHeader(String s) {
        String macroName = MetadataHandler.getHandler().getAppName().toUpperCase() + "_KERNELWRAPPER";
        return String.format(CppSyntax.SIMPLE_HEADER_FILE_STR, macroName, macroName, s);
    }

    private String generateWrappersImplementationBody() {
        StringBuilder sb = new StringBuilder();
        for (NativeFunctionWrapper nativeFunctionWrapper : nativeFunctionWrappers) {
            sb.append(nativeFunctionWrapper.generateNativeMethodImplementation());
        }
        return sb.toString();
    }

    private List<String> getJniHeadersPaths() {
        Iterable<String> jniHeaderPaths = Iterables.transform(jniRootContainer.jniHeaders(), new Function<JniHeader, String>() {
            @Nullable
            @Override
            public String apply(@Nullable JniHeader jniHeader) {
                return jniHeader.jniHeaderFile().getPath();
            }
        });
        return Lists.newArrayList(jniHeaderPaths);
    }

    public KernelFile getKernelFile(){
        return ImmutableKernelFile.builder()
                .kernelWrapperFileName(headerWrapperFileName)
                .userNativeFunctions(userNativeFunctions)
                .nativePath(MetadataHandler.getHandler().getNativePath())
                .build();
    }
}
