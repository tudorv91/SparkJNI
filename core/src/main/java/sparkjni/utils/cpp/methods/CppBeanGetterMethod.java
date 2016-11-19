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

import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.CppSyntax;

import java.lang.reflect.Field;

/**
 * Created by Tudor on 7/23/16.
 */
public class CppBeanGetterMethod extends NativeMethod{
    String returnField;
    Field javaField;

    public CppBeanGetterMethod(CppField field, String accessor){
        if(field != null) {
            methodName = "get" + field.getName();
            returnType = field.getNativeType();
            returnField = field.getName();
            if(returnType.endsWith("[]"))
                returnType = returnType.substring(0, returnType.indexOf("["))+"*";

            javaField = field.getJavaField();
            if(javaField != null)
                ownerClassName = javaField.getDeclaringClass().getSimpleName();
            else
                ownerClassName = field.getOwnerClassName();

            methodBody = constructMethodImpl();
            this.accessor = accessor;
        } else
            isValid = false;
    }

    public static String getJavaClassGetterImpl(String ownerClassName){
        String jClassMethodBody = String.format(CppSyntax.GETTER_FUNCTION_BODY_STR,
                CppSyntax.JAVACLASSJNI_OBJECT_NAME);

        return String.format(CppSyntax.FUNCTION_IMPL_STR,
                "jobject", ownerClassName.substring(3), "getJavaObject", "", jClassMethodBody);
    }

    private String constructMethodBody(String returnField){
        return String.format(CppSyntax.GETTER_FUNCTION_BODY_STR,
                returnField);
    }

    private String constructMethodImpl(){
        return String.format(CppSyntax.FUNCTION_IMPL_STR, returnType, ownerClassName, methodName, "",
                constructMethodBody(returnField));
    }
}
