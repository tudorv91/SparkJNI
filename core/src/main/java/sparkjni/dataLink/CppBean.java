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

package sparkjni.dataLink;

import sparkjni.utils.JniLinkHandler;
import sparkjni.utils.cpp.methods.*;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.cpp.fields.CppRawTypeField;
import sparkjni.utils.cpp.fields.CppReferenceField;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class CppBean {
    private Class javaClass;
    private String cppClassName;

    private ArrayList<CppField> cppFields;
    private ArrayList<String> referencedClasses = new ArrayList<>();
    private HashMap<CppField, CppBeanGetterMethod> fieldsGettersMap;

    private String nativePath = null;
    private String cppImplementation;
    private String headerImplementation;
    private String cppFilePath;
    private String headerFilePath;

    private boolean successful = false;

    private CppConstructor cppConstructor;
    private EmptyCppConstructor emptyCppConstructor;
    private JniConstructor jniConstructor;
    private Destructor destructor;

    public CppBean(Class javaClass, String nativePath) {
        this(javaClass, "CPP" + javaClass.getSimpleName(), nativePath);
    }

    public CppBean(Class javaClass, String cppClassName, String nativePath) {
        this.javaClass = javaClass;
        this.cppClassName = cppClassName;
        this.nativePath = nativePath;
        cppFields = new ArrayList<>();
        cppConstructor = new CppConstructor(this);
        emptyCppConstructor = new EmptyCppConstructor(this);
        jniConstructor = new JniConstructor(this);
        destructor = new Destructor(this);

        if(!createCppFieldsArr()){
            successful = false;
            return;
        }

        createFieldsGettersMapping();

        successful = generateClassDeclaration()
                && generateHeaderFile() && generateFullPath();
    }

    private boolean generateFullPath() {
        if (nativePath != null) {
            String dirPath = nativePath.endsWith("/") ? nativePath : nativePath + "/";
            String headerFileName = cppClassName + ".h";
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
                CppField referenceField = new CppReferenceField(field, JniLinkHandler.getJniLinkHandlerSingleton());
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
        stringBuilder.append(String.format(CppSyntax.JNI_CONSTRUCTOR_PROTOTYPE_STR, cppClassName));
        stringBuilder.append(cppConstructor.generateConstructorWithNativeArgsPrototype());
        stringBuilder.append(emptyCppConstructor.generateNonArgConstructorPrototype());
        stringBuilder.append(String.format(CppSyntax.DESTRUCTOR_PROTOTYPE_STR, cppClassName));

        String publicMemberDeclarations = stringBuilder.toString();
        String macroName = cppClassName.toUpperCase();
        headerImplementation =  String.format(CppSyntax.BEAN_HEADER_FILE_STR, macroName, macroName,includeStatements,
                cppClassName, privateMemberDeclarations, publicMemberDeclarations, macroName);

        return true;
    }

    private String generateMethodPrototypesStatements(){
        StringBuilder stringBuilder = new StringBuilder();
        for(CppBeanGetterMethod nativeMethod : fieldsGettersMap.values()){
            String prototype = String.format(CppSyntax.FUNCTION_PROTOTYPE_STR,
                    "\t",nativeMethod.getReturnType(), nativeMethod.getMethodName(), "");
            stringBuilder.append(prototype);
        }

        stringBuilder.append(String.format(CppSyntax.FUNCTION_PROTOTYPE_STR,
                "\t", "jobject", "getJavaObject", ""));

        return stringBuilder.toString();
    }

    /**
     * Use in header declarations
     * @return
     */
    private String getFieldLineDeclarations() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\tJNIEnv* env;\n");
        stringBuilder.append(String.format("\tjobject %s;\n", CppSyntax.JAVACLASSJNI_OBJECT_NAME));
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
    public String getFieldLineImpl() {
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

    public boolean isSuccessful() {
        return successful;
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

    public void setNativePath(String nativePath){
        this.nativePath = nativePath;
    }

    public String getNativePath(){
        return nativePath;
    }

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
}