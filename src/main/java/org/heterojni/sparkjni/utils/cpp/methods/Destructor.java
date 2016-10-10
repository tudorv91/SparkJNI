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
package org.heterojni.sparkjni.utils.cpp.methods;

import org.heterojni.sparkjni.utils.cpp.fields.CppField;
import org.heterojni.sparkjni.utils.cpp.fields.CppRawTypeField;
import org.heterojni.sparkjni.dataLink.CppBean;
import org.heterojni.sparkjni.utils.CppSyntax;
import org.heterojni.sparkjni.utils.JniUtils;

import java.util.Formatter;

/**
 * Created by root on 8/20/16.
 */
public class Destructor extends NativeMethod{
    public Destructor(CppBean cppBean) {
        super(cppBean);
    }

    public Destructor() {}

    public String getDestructorImpl(){
        StringBuilder sbFormatter = new StringBuilder(),
                bodySb = new StringBuilder();
        Formatter stringFormatter = new Formatter(sbFormatter);

        bodySb.append("\tif(jniCreated != 0){\n");
        for (CppField field : ownerClass.getCppFields()) {
            if(field instanceof CppRawTypeField)
            {
                CppRawTypeField rawTypeField = (CppRawTypeField) field;
                if(rawTypeField.isPrimitiveArray()){
                    bodySb.append(String.format(CppSyntax.RELEASE_ARRAY_STATEMENT_STR,
                            JniUtils.getArrayElementType(rawTypeField.getJavaField()), field.getName() + "Arr", field.getName(), "0"));
                }
            }
        }
        bodySb.append("\t}\n");

        return stringFormatter.format(CppSyntax.DESTRUCTOR_STR,
                ownerClassName, ownerClassName, bodySb.toString()).toString();
    }
}
