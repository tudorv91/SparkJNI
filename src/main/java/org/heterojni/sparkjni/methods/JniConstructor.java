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
package org.heterojni.sparkjni.methods;

import org.heterojni.sparkjni.fields.CppField;
import org.heterojni.sparkjni.utils.CppBean;
import org.heterojni.sparkjni.utils.CppSyntax;
import org.heterojni.sparkjni.fields.CppRawTypeField;
import org.heterojni.sparkjni.fields.CppReferenceField;
import org.heterojni.sparkjni.utils.JniUtils;

/**
 * Created by root on 8/16/16.
 */
public class JniConstructor extends NativeMethod {
    public static final String MUTEX_CPP_FIELD_NAME = "mtx";
    public JniConstructor(CppBean cppBean) {
        super(cppBean);
    }

    public JniConstructor() {}
    // Include it in constructor
    // Here we have unique indentation
    public String generateConstructorImpl() {
        StringBuilder sb = new StringBuilder();
        sb.append(ownerClassName+"::"+ownerClassName);
        sb.append("(" + JniUtils.JNI_CLASS + " replaceMeClassName, " + JniUtils.JNI_OBJECT + " replaceMeObjectName, JNIEnv* " + "env" + "){\n");

        sb.append(CppSyntax.CLASS_REF_ASSIGN_STR);

        /*
        Critical section - synchronized for all instances.
        Here we serialize access to the critical arrays sections from Java.
         */
        sb.append(objectAssignmentSection());
        sb.append("\n");
        sb.append("\t");
        sb.append(MUTEX_CPP_FIELD_NAME);
        sb.append(".lock();\n");

        sb.append(arrayAssignmentSection());

        sb.append("\t");
        sb.append(MUTEX_CPP_FIELD_NAME);
        sb.append(".unlock();\n");

        sb.append("\tthis->env = env;\n");
        sb.append("\tjniCreated = 1;\n");
        sb.append("}");
        return sb.toString();
    }

    private String arrayAssignmentSection() {
        StringBuilder sb = new StringBuilder();
        for (CppField field : ownerClass.getCppFields()) {
            if (field instanceof CppRawTypeField) {
                CppRawTypeField cppRawTypeField = (CppRawTypeField) field;
                if (cppRawTypeField.isArray()) {
                    String jniArrFieldName = field.getName() + "Arr";
                    sb.append(String.format(CppSyntax.REINTERPRET_OBJ_CAST_STR,
                            jniArrFieldName,
                            JniUtils.getArrayTypeDecl(field.getReadableType()),
                            field.getName()));

                    sb.append(String.format(CppSyntax.ENV_GET_ARRAY_LENGTH_STR,
                            field.getName() + "_length", field.getName() + "Arr"));

                    if (cppRawTypeField.isMemoryAligned()) {
                        if (cppRawTypeField.isSafe()) {
                            sb.append(String.format("\t%s temp%s = env->Get%sArrayElements(%s, NULL);\n",
                                    cppRawTypeField.getTypeOfArrayElement().toLowerCase() + "*", cppRawTypeField.getName(), cppRawTypeField.getTypeOfArrayElement(), jniArrFieldName));
                            sb.append(String.format("\tif(posix_memalign((void **)&%s, %d, sizeof(%s) *  %s_length)) " +
                                            "{\n\t\tperror(\"Could not allocate memory..\\n\");\n" +
                                            "\t\treturn;\n" +
                                            "\t}\n",
                                    field.getName(), cppRawTypeField.getMemoryAlignment(), cppRawTypeField.getTypeOfArrayElement().toLowerCase(), field.getName()));

                            sb.append(String.format("\tmemcpy(%s, temp%s, sizeof(%s) * %s_length);\n",
                                    field.getName(), field.getName(), cppRawTypeField.getTypeOfArrayElement().toLowerCase(), cppRawTypeField.getName()));
                        }
                    } else {
                        String typeCast = String.format("(%s)", field.getNativeType());
                        sb.append(String.format(CppSyntax.GET_ARRAY_ELEMENTS_STR,
                                cppRawTypeField.getName(), typeCast, cppRawTypeField.getTypeOfArrayElement(), jniArrFieldName));
                    }
                }
            }
        }

        return sb.toString();
    }

    private String objectAssignmentSection(){
        StringBuilder sb = new StringBuilder();
        // Use annotations to select which fields are to be used
        // in native
        for (CppField field : ownerClass.getCppFields()) {
            if(field instanceof CppRawTypeField) {
                CppRawTypeField cppRawTypeField = (CppRawTypeField) field;
                if (field.isPrimitive() || (field.isArray() && cppRawTypeField.isPrimitiveArray())) {
                    if (!cppRawTypeField.isTranslatedField())
                        continue;

                    sb.append(String.format(CppSyntax.GET_FIELD_ID_STMT_STR,
                            cppRawTypeField.getName(), "replaceMeClassName", cppRawTypeField.getName(),
                            cppRawTypeField.getTypeSignature()));

                    String formatString = (cppRawTypeField.isArray()) ? "jobject %s_obj" : "%s";
                    sb.append(String.format(formatString, cppRawTypeField.getName()));
                    sb.append(String.format(CppSyntax.GET_FIELD_STMT_STR,
                            "env", cppRawTypeField.getJniTypePlaceholderName(),
                            "replaceMeObjectName", cppRawTypeField.getName()));
                }
            } else {
                CppReferenceField cppReferenceField = (CppReferenceField) field;
                sb.append(String.format(CppSyntax.GET_FIELD_ID_STMT_STR,
                        cppReferenceField.getName(), "replaceMeClassName", cppReferenceField.getName(),
                        cppReferenceField.getTypeSignature()));
                sb.append(String.format(CppSyntax.NULL_PTR_CHECK_STR,
                        "j_"+cppReferenceField.getName(), String.format("FieldID object j_%s is null..",
                                cppReferenceField.getName())));
                sb.append(String.format("jobject %s_obj = %s->Get%sField(%s, j_%s);\n",
                        cppReferenceField.getName(), "env", "Object",
                        "replaceMeObjectName", cppReferenceField.getName()));
                sb.append(String.format("\tjclass %sClass = env->GetObjectClass(%s_obj);\n",
                        cppReferenceField.getName(), cppReferenceField.getName()));
                sb.append(String.format("\t%s = new %s(%sClass, %s_obj, %s);\n",
                        cppReferenceField.getName(), cppReferenceField.getReadableType(),
                        cppReferenceField.getName(), cppReferenceField.getName(), "env"));
            }
        }
        return sb.toString();
    }
}
