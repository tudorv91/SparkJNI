/**
 * Copyright 2016 Tudor Alexandru Voicu and Zaid Al-Ars, TUDelft
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sparkjni.dataLink;

import sparkjni.utils.CppSyntax;
import sparkjni.utils.JniUtils;
import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.cpp.methods.*;

import java.util.HashMap;
import java.util.List;

public class CppBean {
    private Class javaClass;
    private String cppClassName;

    private HashMap<CppField, CppBeanGetterMethod> fieldsGettersMap;

    private String nativePath;
    private String cppImplementation;
    private String cppFilePath;
    private String headerFilePath;

    private CppConstructor cppConstructor;
    private EmptyCppConstructor emptyCppConstructor;
    private JniConstructor jniConstructor;
    private Destructor destructor;
    private CppFieldsCreator cppFieldsCreator;

    public CppBean(Class javaClass, String nativePath) {
        this(javaClass, "CPP" + javaClass.getSimpleName(), nativePath);
    }

    public CppBean(Class javaClass, String cppClassName, String nativePath) {
        this.javaClass = javaClass;
        this.cppClassName = cppClassName;
        this.nativePath = nativePath;

        cppConstructor = new CppConstructor(this);
        emptyCppConstructor = new EmptyCppConstructor(this);
        jniConstructor = new JniConstructor(this);
        destructor = new Destructor(this);
        cppFieldsCreator = new CppFieldsCreator(this.javaClass);

        createDeclarationIfFieldsMapIsSuccessful();
    }

    private void createDeclarationIfFieldsMapIsSuccessful() {
        if(cppFieldsCreator.isSuccessful()) {
            createFieldsGettersMapping();
            generateClassDeclaration();
            generateFullPath();
        }
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

    private boolean generateClassDeclaration() {
        StringBuilder stringBuilder = new StringBuilder();

        String[] includedHeaders = new String[cppFieldsCreator.getReferencedClasses().size() + 1];
        for (int idx = 0; idx < cppFieldsCreator.getReferencedClasses().size(); idx++)
            includedHeaders[idx] = cppFieldsCreator.getReferencedClasses().get(idx) + ".h";

        includedHeaders[cppFieldsCreator.getReferencedClasses().size()] = cppClassName + ".h";
        stringBuilder.append(JniUtils.generateIncludeStatements(false,
                includedHeaders));
        stringBuilder.append("\tstd::mutex ");
        stringBuilder.append(String.format("%s::%s", cppClassName, JniConstructor.MUTEX_CPP_FIELD_NAME));
        stringBuilder.append(";\n");
//        stringBuilder.append(getFieldLineImpl());

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

    public String getHeaderImplementation() {
        String macroName = cppClassName.toUpperCase();
        return String.format(CppSyntax.BEAN_HEADER_FILE_STR, macroName, macroName, generateCppIncludeStatements(),
                cppClassName, generatePrivateMemberDeclarations(), generatePublicMemberDeclarations(), macroName);
    }

    private String generatePublicMemberDeclarations() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(generateMethodPrototypesStatements());
        generateConstructors(stringBuilder);
        stringBuilder.append(String.format(CppSyntax.DESTRUCTOR_PROTOTYPE_STR, cppClassName));

        return stringBuilder.toString();
    }

    private String generatePrivateMemberDeclarations() {
        return getFieldLineDeclarations() +
                "\tstatic std::mutex " +
                JniConstructor.MUTEX_CPP_FIELD_NAME +
                ";\n";
    }

    private String generateCppIncludeStatements() {
        String[] includes = new String[cppFieldsCreator.getReferencedClasses().size()];
        int ctr = 0;
        for (String ref : cppFieldsCreator.getReferencedClasses())
            includes[ctr++] = ref + ".h";

        return JniUtils.generateIncludeStatements(true, includes);
    }

    private void generateConstructors(StringBuilder stringBuilder) {
        stringBuilder.append(String.format(CppSyntax.JNI_CONSTRUCTOR_PROTOTYPE_STR, cppClassName));
        stringBuilder.append(cppConstructor.generateConstructorWithNativeArgsPrototype());
        stringBuilder.append(emptyCppConstructor.generateNonArgConstructorPrototype());
    }

    private String generateMethodPrototypesStatements() {
        StringBuilder stringBuilder = new StringBuilder();
        for (CppBeanGetterMethod nativeMethod : fieldsGettersMap.values()) {
            String prototype = String.format(CppSyntax.FUNCTION_PROTOTYPE_STR,
                    "\t", nativeMethod.getReturnType(), nativeMethod.getMethodName(), "");
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
        for (CppField cppField : cppFieldsCreator.getCppFields()) {
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
        for (CppField cppField : cppFieldsCreator.getCppFields()) {
            stringBuilder.append(cppField.getFieldDeclarationCppStmt(cppClassName));
        }
        return "";
    }

    private void createFieldsGettersMapping() {
        if (fieldsGettersMap == null)
            fieldsGettersMap = new HashMap<>();

        for (CppField cppField : cppFieldsCreator.getCppFields()) {
            fieldsGettersMap.put(cppField, new CppBeanGetterMethod(cppField, "RAND"));
        }
    }

    private String getGetterMethods() {
        StringBuilder sb = new StringBuilder();
        for (CppBeanGetterMethod method : fieldsGettersMap.values()) {
            sb.append(method.getMethodBody());
            sb.append("\n");
        }
        sb.append(CppBeanGetterMethod.getJavaClassGetterImpl(cppClassName));
        return sb.toString();
    }

    public CppField getCppFieldByName(String fieldName) {
        return cppFieldsCreator.getCppFieldByName(fieldName);
    }

    public String getCppClassName() {
        return cppClassName;
    }

    public Class getJavaClass() {
        return javaClass;
    }

    public String getCppFilePath() {
        return cppFilePath;
    }

    public String getCppImplementation() {
        return cppImplementation;
    }

    public void setNativePath(String nativePath) {
        this.nativePath = nativePath;
    }

    public String getNativePath() {
        return nativePath;
    }

    public HashMap<CppField, CppBeanGetterMethod> getFieldsGettersMap() {
        return fieldsGettersMap;
    }

    public List<CppField> getCppFields() {
        return cppFieldsCreator.getCppFields();
    }

    public boolean isSuccessful(){
        return cppFieldsCreator.isSuccessful();
    }

    public String getHeaderFilePath() {
        return headerFilePath;
    }
}