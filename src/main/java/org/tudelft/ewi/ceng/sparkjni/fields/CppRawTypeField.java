package org.tudelft.ewi.ceng.sparkjni.fields;//package org.apache.spark.examples;

import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;

import java.lang.reflect.Field;

/**
 * Created by Tudor on 7/6/2016.
 */
public class CppRawTypeField extends CppField {
    boolean isSafe = true;
    boolean isLengthOfArray = false;
    boolean isMemoryAligned = false;
    int memoryAlignment = JniUtils.DEFAULT_MEMORY_ALIGNMENT;

    public CppRawTypeField(Field field) {
        super(field);
        this.type = JniUtils.getCppFieldType(javaField.getType().getSimpleName());
        String annotationOption = javaField.getAnnotation(JNI_field.class).alignment();
        isMemoryAligned = !annotationOption.equals(JniUtils.MEM_UNALIGNED);
        if(isMemoryAligned){
            try {
                memoryAlignment = Integer.parseInt(annotationOption);
            } catch(Exception e){
                isMemoryAligned = false;
            }
        }
        isSafe = javaField.getAnnotation(JNI_field.class).safe();
        String annotationTargetType = javaField.getAnnotation(JNI_field.class).nativeTypeTarget();
//        if(!annotationTargetType.equals(JniUtils.NATIVE_TARGET_TYPE_JAVA_DEFINED))
//            nativeTargetType = annotationTargetType;

        if(nativeTargetType == null)
            fieldDeclaration = String.format(JniUtils.FIELD_DECLARATION_STR, type, "%s"+name);
        else{
            fieldDeclaration = String.format(JniUtils.FIELD_DECLARATION_STR, nativeTargetType, "%s"+name);
        }
        if(javaField.getType().isArray()){
            arrayTypeJniName = JniUtils.getArrayTypeDecl(javaField.getType().getSimpleName());
            String jniArrField = "%s"+name + "Arr";
            fieldDeclaration += String.format(JniUtils.FIELD_DECLARATION_STR, arrayTypeJniName, jniArrField);
        }
    }

    public CppRawTypeField(String cppType, String fieldName, String ownerClassName) {
        super(cppType, fieldName, ownerClassName);
    }

    public String getTypeOfArrayElement() {
        if(isArray())
            return JniUtils.getArrayElementType(javaField);
        else
            throw new RuntimeException("[ERROR] Field is not an array..");
    }

    public String getArrayTypeName(){
//        if(nativeTargetType == null)
            return arrayTypeJniName;
//        else
//            return nativeTargetType;
    }

    public boolean isPrimitiveArray(){
        if(isArray())
            return JniUtils.isPrimitiveArray(javaField.getType());
        else
            return false;
    }

    public boolean isTranslatedField(){
        return translatedField;
    }

    public String getJniTypePlaceholderName(){
        if(javaField == null)
            return "Int";
        else return JniUtils.getJniTypePlaceholderName(javaField.getType());
    }

    public boolean isMemoryAligned() {
        return isMemoryAligned;
    }

    public int getMemoryAlignment() {
        return memoryAlignment;
    }

    public boolean isSafe() {
        return isSafe;
    }

    //    /**
//     * Constructor designed to be used for creating variables
//     * that tell the length of the @field array.
//     * @param cppType
//     * @param fieldName
//     */
//    public CppRawTypeField(String cppType, String fieldName, String ownerClassName){
//        this.readableType = cppType;
//        this.name = fieldName;
//        this.type = cppType;
//        this.ownerClassName = ownerClassName;
//        fieldDeclaration = String.format(JniUtils.FIELD_DECLARATION_STR, type, "%s"+name);
//    }
    public CppRawTypeField getArrayLengthFieldCppFieldObj(){
        return new CppRawTypeField("int", javaField.getName()+"_length", ownerClassName);
    }
}