package sparkjni.dataLink;

import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.cpp.fields.CppRawTypeField;
import sparkjni.utils.cpp.fields.CppReferenceField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static sparkjni.utils.JniUtils.isPrimitiveArray;

public class CppFieldsCreator {
    private final Class javaClass;

    private List<CppField> cppFields;

    private List<String> referencedClasses;
    public CppFieldsCreator(Class javaClass) {
        this.javaClass = javaClass;
        process();
    }

    private void process() {
        referencedClasses = new ArrayList<>();
        cppFields = new ArrayList<>();

        for (Field field : javaClass.getDeclaredFields()) {
            if (field.getAnnotation(JNI_field.class) == null) {
                continue;
            }

            Class fieldType = field.getType();
            if (fieldType.isPrimitive()) {
                cppFields.add(new CppRawTypeField(field));
            } else if (fieldType.isArray() && isPrimitiveArray(fieldType)) {
                CppRawTypeField cppArrField = new CppRawTypeField(field);
                cppFields.add(cppArrField);
                cppFields.add(cppArrField.getArrayLengthFieldCppFieldObj());
            } else {
                CppField referenceField = new CppReferenceField(field);
                if (referenceField.hadValidNativeMapper()) {
                    cppFields.add(referenceField);
                    referencedClasses.add(referenceField.getReadableType());
                }
            }
        }
    }

    public CppField getCppFieldByName(String fieldName) {
        for (CppField field : cppFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;

    }

    public List<String> getReferencedClasses() {
        return referencedClasses;
    }

    public List<CppField> getCppFields() {
        return cppFields;
    }
}
