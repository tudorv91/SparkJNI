package org.tudelft.ewi.ceng.sparkjni.utils;

import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.fields.CppField;
import org.tudelft.ewi.ceng.sparkjni.fields.CppRawTypeField;
import org.tudelft.ewi.ceng.sparkjni.fields.CppReferenceField;
import org.tudelft.ewi.ceng.sparkjni.methods.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tudor on 7/5/2016.
 */
public class CppClass {
    private Class javaClass;
    private String cppClassName;

    private ArrayList<CppField> cppFields;
    private ArrayList<String> referencedClasses = new ArrayList<>();
    private HashMap<CppField, CppBeanGetterMethod> fieldsGettersMap;

    private static String nativeLibPath = null;
    private String cppImplementation;
    private String headerImplementation;
    private String cppFilePath;
    private String headerFileName;

    private String headerFilePath;

    public boolean isSuccesful() {
        return succesful;
    }

    private boolean succesful = false;

    private CppConstructor cppConstructor;
    private EmptyCppConstructor emptyCppConstructor;
    private JniConstructor jniConstructor;
    private Destructor destructor;

    public HashMap<CppField, CppBeanGetterMethod> getFieldsGettersMap() {
        return fieldsGettersMap;
    }

    public ArrayList<CppField> getCppFields() {
        return cppFields;
    }
    public String getHeaderFilePath() {
        return headerFilePath;
    }

    public String getHeaderImplementation() {
        return headerImplementation;
    }

    public CppClass(Class javaClass) {
        this(javaClass, "CPP" + javaClass.getSimpleName());
    }

    public CppClass(Class javaClass, String cppClassName) {
        this.javaClass = javaClass;
        this.cppClassName = cppClassName;
        cppFields = new ArrayList<>();
        cppConstructor = new CppConstructor(this);
        emptyCppConstructor = new EmptyCppConstructor(this);
        jniConstructor = new JniConstructor(this);
        destructor = new Destructor(this);

        if(!createCppFieldsArr()){
            succesful = false;
            return;
        }
        createFieldsGettersMapping();

        succesful = generateClassDeclaration()
                && generateHeaderFile() && generateFullPath();
    }

    public String getCppClassName() {
        return cppClassName;
    }

    public Class getJavaClass(){
        return javaClass;
    }

    public String getCppFilePath() {
        return cppFilePath;
    }

    public String getCppImplementation() {
        return cppImplementation;
    }

    public static void setNativeLibPath(String nativeLibPath){
        CppClass.nativeLibPath = nativeLibPath;
    }

    public static String getNativeLibPath(){
        return nativeLibPath;
    }

    private boolean generateFullPath() {
        if (nativeLibPath != null) {
            String dirPath = nativeLibPath.endsWith("/") ? nativeLibPath : nativeLibPath + "/";
            headerFileName = cppClassName + ".h";
            cppFilePath = dirPath + cppClassName + ".cpp";
            headerFilePath = dirPath + headerFileName;
            return true;
        }
        return false;
    }

    private boolean createCppFieldsArr() {
        Field[] fields = javaClass.getDeclaredFields();

        for (Field field : fields) {
            Annotation jniAnnotation = field.getAnnotation(JNI_field.class);
            if(jniAnnotation == null)
                continue;

            Class fieldType = field.getType();
            if(fieldType.isPrimitive()) {
                cppFields.add(new CppRawTypeField(field));
            } else if(fieldType.isArray() && JniUtils.isPrimitiveArray(fieldType)){
                CppRawTypeField cppArrField = new CppRawTypeField(field);
                cppFields.add(cppArrField);
                cppFields.add(cppArrField.getArrayLengthFieldCppFieldObj());
            } else {
                CppField referenceField = new CppReferenceField(field);
                if(referenceField.hadValidNativeMapper()){
                    cppFields.add(referenceField);
                    referencedClasses.add(referenceField.getReadableType());
                } else
                    return false;
            }
        }
        return true;
    }

    private boolean generateClassDeclaration() {
        StringBuilder stringBuilder = new StringBuilder();

        String[] includedHeaders = new String[referencedClasses.size() + 1];
        for(int idx = 0; idx < referencedClasses.size(); idx++)
            includedHeaders[idx] = referencedClasses.get(idx)+".h";

        includedHeaders[referencedClasses.size()] = cppClassName + ".h";
        stringBuilder.append(JniUtils.generateIncludeStatements(false,
                includedHeaders));
        stringBuilder.append("\tstd::mutex ");
        stringBuilder.append(String.format("%s::%s", cppClassName, JniConstructor.MUTEX_CPP_FIELD_NAME));
        stringBuilder.append(";\n");
        stringBuilder.append(getFieldLineImpl());

        // JNI intervention section
        stringBuilder.append(jniConstructor.generateConstructorImpl());
        stringBuilder.append("\n");

        // User-exposed section
        stringBuilder.append(getGetterMethods());
        stringBuilder.append(cppConstructor.generateConstructorWithNativeArgsImpl());
        stringBuilder.append(emptyCppConstructor.generateNonArgConstructorImpl());
        stringBuilder.append(destructor.getDestructorImpl());

        cppImplementation = stringBuilder.toString();

        return true;
    }

    private boolean generateHeaderFile(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFieldLineDeclarations());

        stringBuilder.append("\tstatic std::mutex ");
        stringBuilder.append(JniConstructor.MUTEX_CPP_FIELD_NAME);
        stringBuilder.append(";\n");

        String []includes = new String[referencedClasses.size()];
        int ctr = 0;
        for(String ref: referencedClasses)
            includes[ctr++] = ref+".h";

        String includeStatements = JniUtils.generateIncludeStatements(true, includes),
                privateMemberDeclarations = stringBuilder.toString();

        stringBuilder = new StringBuilder();

        stringBuilder.append(generateMethodPrototypesStatements());
        stringBuilder.append(String.format(JniUtils.JNI_CONSTRUCTOR_PROTOTYPE_STR, cppClassName));
        stringBuilder.append(cppConstructor.generateConstructorWithNativeArgsPrototype());
        stringBuilder.append(emptyCppConstructor.generateNonArgConstructorPrototype());
        stringBuilder.append(String.format(JniUtils.DESTRUCTOR_PROTOTYPE_STR, cppClassName));

        String publicMemberDeclarations = stringBuilder.toString();
        String macroName = cppClassName.toUpperCase();
        headerImplementation =  String.format(JniUtils.BEAN_HEADER_FILE_STR, macroName, macroName,includeStatements,
                cppClassName, privateMemberDeclarations, publicMemberDeclarations, macroName);

        return true;
    }

    private String generateMethodPrototypesStatements(){
        StringBuilder stringBuilder = new StringBuilder();
        for(CppBeanGetterMethod nativeMethod : fieldsGettersMap.values()){
            String prototype = String.format(JniUtils.FUNCTION_PROTOTYPE_STR,
                    nativeMethod.getReturnType(), nativeMethod.getMethodName(), "");
            stringBuilder.append(prototype);
        }

        stringBuilder.append(String.format(JniUtils.FUNCTION_PROTOTYPE_STR,
                "jobject", "getJavaObject", ""));

        return stringBuilder.toString();
    }

    /**
     * Use in header declarations
     * @return
     */
    private String getFieldLineDeclarations() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\tJNIEnv* env;\n");
        stringBuilder.append(String.format("\tjobject %s;\n", JniUtils.JAVACLASSJNI_OBJECT_NAME));
        stringBuilder.append("\tint jniCreated = 0;\n");
        for(CppField cppField: cppFields){
            stringBuilder.append(cppField.getFieldDeclarationStmt());
        }
        return stringBuilder.toString();
    }

    /**
     * Use in cpp files declarations
     * @return
     */
    private String getFieldLineImpl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("\tJNIEnv* %s::env;\n", cppClassName));
        for(CppField cppField: cppFields){
            stringBuilder.append(cppField.getFieldDeclarationCppStmt(cppClassName));
        }
        return "";
    }

    private void createFieldsGettersMapping(){
        if(fieldsGettersMap == null)
            fieldsGettersMap = new HashMap<>();

        for(CppField cppField: cppFields){
            fieldsGettersMap.put(cppField, new CppBeanGetterMethod(cppField, "RAND"));
        }
    }

    private String getGetterMethods() {
        StringBuilder sb = new StringBuilder();
        for(CppBeanGetterMethod method: fieldsGettersMap.values()){
            sb.append(method.getMethodBody());
            sb.append("\n");
        }
        sb.append(CppBeanGetterMethod.getJavaClassGetterImpl(cppClassName));
        return sb.toString();
    }

    public CppField getCppFieldByName(String fieldName){
        for(CppField field: cppFields)
            if(field.getName().equals(fieldName))
                return field;

        return null;
    }
}