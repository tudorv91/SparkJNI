package org.tudelft.ewi.ceng.sparkjni.methods;

import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;
import org.tudelft.ewi.ceng.sparkjni.fields.CppField;
import org.tudelft.ewi.ceng.sparkjni.fields.CppRawTypeField;

import java.util.Formatter;

/**
 * Created by root on 8/20/16.
 */
public class Destructor extends NativeMethod{
    public Destructor(CppClass cppClass) {
        super(cppClass);
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
                    bodySb.append(String.format(JniUtils.RELEASE_ARRAY_STATEMENT_STR,
                            JniUtils.getArrayElementType(rawTypeField.getJavaField()), field.getName() + "Arr", field.getName(), "0"));
                }
            }
        }
        bodySb.append("\t}\n");

        return stringFormatter.format(JniUtils.DESTRUCTOR_STR,
                ownerClassName, ownerClassName, bodySb.toString()).toString();
    }
}
