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
package sparkjni.utils.cpp.methods;

import sparkjni.utils.MetadataHandler;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.utils.exceptions.Messages;
import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.cpp.fields.CppRawTypeField;
import sparkjni.utils.cpp.fields.CppReferenceField;
import sparkjni.dataLink.CppBean;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Tudor on 8/6/16.
 */
public class CppConstructor extends NativeMethod {

    public static final String AUTO_PARAM_JAVA7_ERR = "Automatically retrieving parameter names is not possible in Java 7." +
            "\nConsider switching to Java 8 and change implementation or define target with the field target name..";
    public static final String ERROR_NO_VALID_CONSTRUCTOR = "No valid constructor found in Java class. " +
            "Please annotate it with @JNI_method";

    public CppConstructor(CppBean cppBean) {
        super(cppBean);
    }

    private String generateArgsForNativeConstructor() {
        StringBuilder argStringBuilder = new StringBuilder();
        for (CppField cppField : ownerClass.getCppFields()) {
            if(cppField instanceof CppRawTypeField)
            argStringBuilder.append(String.format("%s %s, ",
                    JniUtils.getCppFieldType(cppField.getReadableType()), cppField.getName() + "arg"));
            else
                argStringBuilder.append(String.format("%s %s, ",
                        JniUtils.getCppFieldType(cppField.getNativeType()), cppField.getName() + "arg"));
        }

        argStringBuilder.append("jclass jClass, ");
        argStringBuilder.append(String.format("JNIEnv* %s", CppSyntax.JNI_ENV_CONSTRUCTOR_ARG));

        return argStringBuilder.toString();
    }

    public String generateConstructorWithNativeArgsPrototype() {
        String argsList = generateArgsForNativeConstructor();
        return String.format(CppSyntax.CONSTRUCTOR_WITH_NATIVE_ARGS_PROTOTYPE_STR,
                ownerClass.getCppClassName(), argsList);
    }

    public String generateConstructorWithNativeArgsImpl() {
        String argsList = generateArgsForNativeConstructor();
        StringBuilder constructorBodyBuilder = new StringBuilder();
        for (CppField cppField : ownerClass.getFieldsGettersMap().keySet()) {
            constructorBodyBuilder.append(String.format(CppSyntax.CONSTRUCTOR_STMT_STR,
                    cppField.getName(), cppField.getName() + "arg"));
        }

        constructorBodyBuilder.append(String.format(CppSyntax.CONSTRUCTOR_STMT_STR,
                CppSyntax.JNI_ENV_OBJ_NAME, CppSyntax.JNI_ENV_CONSTRUCTOR_ARG));
        constructorBodyBuilder.append(String.format(CppSyntax.NULL_PTR_CHECK_STR,
                "jClass", Messages.ERR_PROVIDED_JCLASS_NULL_STR));

        Constructor jniEnabledConstructor = null;
        for (Constructor constructor : ownerClass.getJavaClass().getDeclaredConstructors()) {
            if (constructor.getAnnotation(JNI_method.class) != null) {
                jniEnabledConstructor = constructor;
                break;
            }
        }

        if (jniEnabledConstructor == null)
            throw new RuntimeException(ERROR_NO_VALID_CONSTRUCTOR);

        String constructorSig = String.format(", \"%s\"", generateMethodSignature(jniEnabledConstructor));

        Class[] paramTypes = jniEnabledConstructor.getParameterTypes();

        Annotation[][] paramAnnotations = jniEnabledConstructor.getParameterAnnotations();

        LinkedHashMap<String, String> fieldsParamsMap = new LinkedHashMap<>();
        for (int idx = 0; idx < paramTypes.length; idx++) {
            for (int aIdx = 0; aIdx < paramAnnotations[idx].length; aIdx++) {
                if (paramAnnotations[idx][aIdx].annotationType().equals(JNI_param.class)) {
                    JNI_param param = (JNI_param) paramAnnotations[idx][aIdx];
                    String paramFieldTarget = param.target();
                    if(paramFieldTarget.equals(CppSyntax.CONSTRUCTOR_PARAM_DEFAULT_NAME_MAPPING)){
                        // Not possible in Java 7.
                        // Fill using [].getParameters() and retrieve names
                        throw new RuntimeException(AUTO_PARAM_JAVA7_ERR);
                    }
                    else if(paramTypes[idx].isPrimitive() || JniUtils.isPrimitiveArray(paramTypes[idx]))
                        fieldsParamsMap.put(paramFieldTarget, paramTypes[idx].getSimpleName());
                    else {
                        CppBean cppBean = new CppBean(paramTypes[idx], MetadataHandler.getHandler().getNativePath());
                        fieldsParamsMap.put(paramFieldTarget, cppBean.getCppClassName());
                    }
                }
            }
        }

        constructorBodyBuilder.append(String.format(CppSyntax.DEFINITION_STMT_ENV_GET_STR,
                CppSyntax.JMETHOD_ID, CppSyntax.CONSTRUCTOR_OBJ_NAME, CppSyntax.JNI_ENV_OBJ_NAME,
                CppSyntax.JNI_GET_METHOD_ID, "jClass, " + CppSyntax.JNI_CONSTRUCTOR_NAME + constructorSig));
        constructorBodyBuilder.append(String.format(CppSyntax.NULL_PTR_CHECK_STR,
                CppSyntax.CONSTRUCTOR_OBJ_NAME, Messages.ERR_CONSTRUCTOR_OBJECT_METHOD_IS_NULL_STR));

        String newObjArgs = "";
        for (Map.Entry<String, String> paramField : fieldsParamsMap.entrySet()) {
            CppField cppField = ownerClass.getCppFieldByName(paramField.getKey());
            if (cppField == null)
                throw new RuntimeException(String.format("Field %s not found", paramField.getValue()));
            if(cppField instanceof CppRawTypeField) {
                CppRawTypeField cppRawTypeField = (CppRawTypeField) cppField;
                if (cppRawTypeField.isTranslatedField()) {
                    if (cppField.isArray()) {
                        String arrayTypeNameJNI = cppRawTypeField.getArrayTypeName();
                        String typeOfArrayElement = cppRawTypeField.getTypeOfArrayElement();
                        String setArrRegionMethodName = String.format(CppSyntax.SET_S_ARRAY_REGION_STR,
                                typeOfArrayElement);
                        String newTypeArray = String.format(CppSyntax.NEW_S_ARRAY_STR, typeOfArrayElement);

                        constructorBodyBuilder.append(String.format(CppSyntax.DEFINITION_STMT_ENV_GET_STR,
                                arrayTypeNameJNI, cppField.getName() + "Arr", CppSyntax.JNI_ENV_OBJ_NAME,
                                newTypeArray, cppField.getName() + "_lengtharg"));
                        constructorBodyBuilder.append(String.format(CppSyntax.CALL_METHOD_4ARGS_STR,
                                CppSyntax.JNI_ENV_OBJ_NAME, setArrRegionMethodName, cppField.getName() + "Arr", 0,
                                cppField.getName() + "_lengtharg", cppField.getName()));
                        newObjArgs += cppField.getName() + "Arr, ";
                    } else
                        newObjArgs += cppField.getName() + ", ";
                }
            } else {
                CppReferenceField cppReferenceField = (CppReferenceField) cppField;
                newObjArgs += String.format("%s->getJavaObject(), ", cppReferenceField.getName());
            }
        }

        if (newObjArgs.length() > 2)
            newObjArgs = newObjArgs.substring(0, newObjArgs.length() - 2);
        constructorBodyBuilder.append(String.format("\t%s = %s->NewObject(jClass, constructor, %s);\n",
                CppSyntax.JAVACLASSJNI_OBJECT_NAME, CppSyntax.JNI_ENV_OBJ_NAME, newObjArgs));

        return String.format(CppSyntax.CONSTRUCTOR_WITH_NATIVE_ARGS_IMPL_STR,
                ownerClassName, ownerClassName, argsList, constructorBodyBuilder.toString());
    }

    // TO_DO: use proper polymorphism
    private String generateMethodSignature(Object method) {
        StringBuilder sb = new StringBuilder();

        if (method instanceof Constructor) {
            Constructor constructor = (Constructor) method;
            for (Class paramType : constructor.getParameterTypes()) {
                sb.append(JniUtils.getSignatureForType(paramType));
            }
            return String.format(CppSyntax.JNI_METHOD_SIGNATURE_STR,
                    sb.toString(), JniUtils.getSignatureForType(Void.class));
        } else if (method instanceof Method) {
            Method m = (Method) method;
            for (Class paramType : m.getParameterTypes()) {
                sb.append(JniUtils.getSignatureForType(paramType));
            }
            return String.format(CppSyntax.JNI_METHOD_SIGNATURE_STR,
                    sb.toString(), JniUtils.getSignatureForType(m.getReturnType()));
        } else
            return "INVALID";
    }
}
