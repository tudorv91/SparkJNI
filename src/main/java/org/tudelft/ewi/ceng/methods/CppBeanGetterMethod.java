package org.tudelft.ewi.ceng.methods;

import org.tudelft.ewi.ceng.fields.CppField;
import org.tudelft.ewi.ceng.fields.CppRawTypeField;
import org.tudelft.ewi.ceng.JniUtils;

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
        String jClassMethodBody = String.format(JniUtils.GETTER_FUNCTION_BODY_STR,
                JniUtils.JAVACLASSJNI_OBJECT_NAME);

        return String.format(JniUtils.FUNCTION_IMPL_STR,
                "jobject", ownerClassName.substring(3), "getJavaObject", "", jClassMethodBody);
    }

    private String constructMethodBody(String returnField){
        return String.format(JniUtils.GETTER_FUNCTION_BODY_STR,
                returnField);
    }

    private String constructMethodImpl(){
        return String.format(JniUtils.FUNCTION_IMPL_STR, returnType, ownerClassName, methodName, "",
                constructMethodBody(returnField));
    }
}
