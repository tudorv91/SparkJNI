/**
 * Copyright 2016 Tudor Alexandru Voicu
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
package org.tudelft.ewi.ceng.sparkjni.fields;

import org.tudelft.ewi.ceng.sparkjni.utils.JniFrameworkLoader;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

import java.lang.reflect.Field;

/**
 * Created by root on 8/16/16.
 */
public class CppReferenceField extends CppField {
    public CppReferenceField(Field field) {
        super(field);
        type = JniUtils.getCppReferenceTypeName(javaField.getType());
        if(!JniFrameworkLoader.isTypeRegistered(type))
            return;

        this.readableType = type;
        type = type + "*";
        fieldDeclaration = String.format(JniUtils.FIELD_DECLARATION_STR, type, "%s"+name);
        validNativeMapper = true;
    }
}
