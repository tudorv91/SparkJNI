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
import sparkjni.jniLink.linkContainers.*;
import sparkjni.utils.JniLinkHandler;
import sparkjni.utils.JniUtils;
import sparkjni.utils.exceptions.Messages;
import sparkjni.utils.exceptions.SoftSparkJniException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Value.Immutable
public abstract class FunctionSignatureMapperProvider {
    abstract String[] tokens();
    abstract String parametersLine();
    abstract String fullyQualifiedJavaClass();

    /**
     * DO NOT USE this method here. Use it with ImmutableAbstractFunctionSignatureMapperProvider.
     * @return
     * @throws Exception
     */
    public FunctionSignatureMapper buildFunctionSignatureMapper() throws Exception {
        String[] tokens = tokens();
        String parametersLine = parametersLine();
        String fullyQualifiedJavaClass = fullyQualifiedJavaClass();

        String jniFuncName = tokens[tokens.length - 1];
        String jniDefinedReturnType = tokens[1];
        String[] aux = jniFuncName.split("_");
        String definingJavaMethodName = aux[aux.length - 1];
        String[] params = parametersLine.split(", ");
        List<TypeMapper> parameterList = new ArrayList<>();
        int javaFuncParamNo = params.length - 2;
        if(javaFuncParamNo < 0) {
            throw new SoftSparkJniException(
                    String.format(Messages.ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE, jniFuncName, parametersLine));
        }
        Class enclosingClass = JniLinkHandler.getJniLinkHandlerSingleton().getJavaClassByName(fullyQualifiedJavaClass);
        Method jniMethod = JniUtils.getClassMethodyName(enclosingClass, definingJavaMethodName);
        for(Class parameterType: jniMethod.getParameterTypes()){
            try {
                TypeMapper typeMapper = ImmutableTypeMapper.builder()
                        .javaType(parameterType)
                        .cppType(JniLinkHandler.getJniLinkHandlerSingleton().getContainerByJavaClass(parameterType))
                        .jniType("jobject")
                        .build();
                parameterList.add(typeMapper);
            } catch (NullPointerException e){}
        }

        EntityNameMapper functionNameMapper = ImmutableEntityNameMapper.builder()
                .javaName(definingJavaMethodName)
                .cppName(definingJavaMethodName)
                .jniName(jniFuncName)
                .build();

        CppBean returnCppType = JniLinkHandler.getJniLinkHandlerSingleton().getContainerByJavaClass(jniMethod.getReturnType());

        TypeMapper returnTypeMapper = ImmutableTypeMapper.builder()
                .cppType(returnCppType)
                .javaType(jniMethod.getReturnType())
                .jniType(jniDefinedReturnType)
                .build();

        return ImmutableFunctionSignatureMapper.builder()
                .returnTypeMapper(returnTypeMapper)
                .staticMethod(params[1].equals("jclass"))
                .parameterList(parameterList)
                .functionNameMapper(functionNameMapper)
                .build();
    }
}
