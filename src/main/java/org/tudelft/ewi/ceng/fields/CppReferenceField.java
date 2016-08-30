package org.tudelft.ewi.ceng.fields;

import org.tudelft.ewi.ceng.JniFrameworkLoader;
import org.tudelft.ewi.ceng.JniUtils;
import org.tudelft.ewi.ceng.annotations.JNI_field;

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
