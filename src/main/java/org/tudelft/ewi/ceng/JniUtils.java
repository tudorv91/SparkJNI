package org.tudelft.ewi.ceng;//package org.apache.spark.examples;

import java.lang.reflect.Field;

/**
 * Created by Tudor on 7/6/2016.
 */
public class JniUtils {
    public static final String MEM_UNALIGNED = "";

    public static final String MEM_ALIGNED_8 = "8";
    public static final String MEM_ALIGNED_16 = "16";
    public static final String MEM_ALIGNED_32 = "32";
    public static final String MEM_ALIGNED_64 = "64";
    public static final String MEM_ALIGNED_128 = "128";
    public static final String MEM_ALIGNED_256 = "256";

    public static final String ARRAY_CRITICAL = "critical";
    public static final String ARRAY_NORMAL = "normal";

    public static final String DESTRUCTOR_STR = "%s::~%s() {\n%s}\n";
    public static final String DESTRUCTOR_PROTOTYPE_STR = "\t~%s();\n";
    public static final String JNI_CONSTRUCTOR_PROTOTYPE_STR =
            "\t%s(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);\n";
    public static final String CONSTRUCTOR_WITH_NATIVE_ARGS_PROTOTYPE_STR =
            "\t%s(%s);\n"; // here we have variable number of arguments - personalized by user classes
    public static final String CONSTRUCTOR_WITH_NATIVE_ARGS_IMPL_STR =
            "%s::%s(%s){\n%s}\n"; // here we have variable number of arguments - personalized by user classes
    public static final String CONSTRUCTOR_STMT_STR = "\t%s = %s;\n";
    public static final String RELEASE_ARRAY_STATEMENT_STR = "env->Release%sArrayElements(%s, %s, %s);";
    // headers, user-defined prototypes, JNI function calls and user-defined functions
    public static final String CPP_OUT_FILE_STR = "%s\n%s\n%s\n%s";
    public static final String JNI_CONSTRUCTOR_IMPL_STR = "%s (JNIEnv* env, jobject thisObj, jobject bean){\n%s}";
    public static final String JNI_FUNCTION_BODY_STR = "%s\n\treturn bean;\n";
    public static final String JNI_FUNCTION_BODY_STMT_STR = "\tjclass beanClass%d = env->GetObjectClass(bean);\n" +
            "\t%s cppBean(beanClass%d, bean, env);\n";
    public static final String BEAN_HEADER_FILE_STR = "#ifndef %s\n#define %s\n" +
            "%s\nclass %s {\n" + // include statements and class name
            "private:\n%s\n" +
            "public:\n%s\n};\n" +
            "#endif";
    public static final String BEAN_CPP_FILE_STR = "%s\n" + // includes
            "%s\n" +                                        // fields
            "%s\n";                                         // functions
    public static final String FUNCTION_PROTOTYPE_STR = "\t\t%s %s(%s);\n";
    public static final String FUNCTION_IMPL_STR = "\t%s CPP%s::%s(%s){\n%s}\n";
    public static final String GETTER_FUNCTION_BODY_STR = "\t\treturn %s;\n\t";
    public static final String FIELD_DECLARATION_STR = "\t%s %s;\n";

    public static final String NULL_PTR_CHECK_STR = "\tif(%s == NULL){\n" +
            "\t\tprintf(\"%s\");\n" +
            "\t\treturn;\n" +
            "\t}\n";

    public static final String JAVACLASSJNI_OBJECT_NAME = "jniJavaClassRef";

    public static final String JNI_FALSE = "JNI_FALSE";
    public static final String JNI_TRUE = "JNI_TRUE";

    public static final String JNI_OBJECT = "jobject";
    public static final String JNI_CLASS = "jclass";
    public static final String JNI_STRING = "jstring";
    public static final String JNI_ARRAY = "jarray";
    public static final String DEFAULT_INCLUDE_STATEMENTS = "#include <stdio.h>\n" +
            "#include <stdlib.h>\n" +
            "#include <string.h>\n" +
            "#include <iostream>\n" +
            "#include <stdint.h>\n" +
            "#include <jni.h>\n" +
            "#include <mutex>\n\n";
    public static final String JNI_ENV_OBJ_NAME = "env";
    public static final String JNI_ENV_CONSTRUCTOR_ARG = "jniEnv";
    public static final String DEFINITION_STMT_ENV_GET_STR = "\t%s %s = %s->%s(%s);\n";
    public static final String JMETHOD_ID = "jmethodID";
    public static final String CONSTRUCTOR_OBJ_NAME = "constructor";
    public static final String JNI_GET_METHOD_ID = "GetMethodID";
    public static final String JNI_CONSTRUCTOR_NAME = "\"<init>\"";
    public static final String PROVIDED_JCLASS_NULL_STR = "Provided java class object is null..!";
    public static final String JNI_METHOD_SIGNATURE_STR = "(%s)%s";
    public static final String ENV_GET_ARRAY_LENGTH_STR = "\t%s = env->GetArrayLength(%s);\n";
    public static final String SET_S_ARRAY_REGION_STR = "Set%sArrayRegion";
    public static final String CALL_METHOD_4ARGS_STR = "\t%s->%s(%s, %s, %s, %s);\n";
    public static final String NEW_S_ARRAY_STR = "New%sArray";
    public static final String CONSTRUCTOR_OBJECT_METHOD_IS_NULL_STR = "Constructor object method is null";
    public static final String REINTERPRET_OBJ_CAST_STR = "\t%s = reinterpret_cast<%s>(%s_obj);\n";
    public static final String GET_ARRAY_ELEMENTS_STR = "\t%s = %senv->Get%sArrayElements(%s, NULL);\n";
    public static final String CLASS_REF_ASSIGN_STR = "\n\tjniJavaClassRef = replaceMeObjectName;\n";
    public static final String GET_FIELD_STMT_STR = " = %s->Get%sField(%s, j_%s);\n";
    public static final String GET_FIELD_ID_STMT_STR = "\tjfieldID j_%s = env->GetFieldID(%s, \"%s\", \"%s\");\n\t";
    public static final int DEFAULT_MEMORY_ALIGNMENT = 64;
    public static final String NATIVE_TARGET_TYPE_JAVA_DEFINED = "javaDefined";
    public static final String CONSTRUCTOR_PARAM_DEFAULT_NAME_MAPPING = "javaParamName";

    public static String getArrayElementType(Field field) {
        if (!field.getType().isArray())
            throw new IllegalArgumentException(String.format("Field %s with type %s is not an array..",
                    field.getName(), field.getType().getName()));

        switch (field.getType().getName()) {
            case "[Z":
                return "Boolean";
            case "[C":
                return "Char";
            case "[S":
                return "Short";
            case "[I":
                return "Int";
            case "[J":
                return "Long";
            case "[F":
                return "Float";
            case "[D":
                return "Double";
            case "[B":
                return "Byte";
            default:
                return "INVALID";
        }
    }

    public static String generateIncludeStatements(boolean includeExternalLibs, String[] jniHeaderFiles) {
        StringBuilder sb = new StringBuilder();

        if (includeExternalLibs)
            sb.append(DEFAULT_INCLUDE_STATEMENTS);

        if (jniHeaderFiles == null)
            return sb.toString();

        for (String file : jniHeaderFiles)
            sb.append(String.format("#include \"%s\"\n", file));

        return sb.toString();
    }

    public static String getArrayTypeDecl(String arrayType) {
        switch (arrayType) {
            case "int[]":
                return "jintArray";
            case "double[]":
                return "jdoubleArray";
            case "byte[]":
                return "jbyteArray";
            case "short[]":
                return "jshortArray";
            case "long[]":
                return "jlongArray";
            case "float[]":
                return "jfloatArray";
            case "char[]":
                return "jcharArray";
            case "boolean[]":
                return "jbooleanArray";
            default:
                return "INVALID";
        }
    }

    public static String getCppFieldType(String javaFieldType) {
        if(javaFieldType.equals("byte"))
            return "jbyte";
        if(javaFieldType.equals("byte[]"))
            return "jbyte*";
        if (javaFieldType.endsWith("[]")) {
            return javaFieldType.replace("[]", "*");
        } else
            return javaFieldType;
    }

    public static boolean isPrimitiveArray(Class fieldType) {
        switch (fieldType.getName()) {
            case "[Z":
            case "[C":
            case "[S":
            case "[I":
            case "[J":
            case "[F":
            case "[D":
            case "[B":
                return true;
            default:
                return false;
        }
    }

    public static String getSignatureForType(Class type) {
        if (type.isPrimitive())
            switch (type.getSimpleName()) {
                case "boolean":
                    return "Z";
                case "byte":
                    return "B";
                case "char":
                    return "C";
                case "short":
                    return "S";
                case "int":
                    return "I";
                case "long":
                    return "J";
                case "float":
                    return "F";
                case "double":
                    return "D";
                default:
                    return "INVALID";
            }
        else if (type.isArray())
            return "[" + getSignatureForType(type.getComponentType());
        else if(type.getSimpleName().equals("Void"))
            return "V";
        else {
            String javaFullyQualName = type.getName();
            String[] path = javaFullyQualName.split("\\.");
            StringBuilder sb = new StringBuilder();
            sb.append("L");
            for(int idx = 0; idx < path.length - 1; idx++)
                sb.append(String.format("%s/", path[idx]));

            if(path.length > 0)
                sb.append(path[path.length - 1]);
            sb.append(";");
            return sb.toString();
        }
    }

    public static String getJniTypePlaceholderName(Class cls) {
        if (cls.isPrimitive()) {
            String sn = cls.getSimpleName();
            return sn.substring(0, 1).toUpperCase() + sn.substring(1);
        } else if (cls.isArray()) {
            return "Object";
        } else
            return null;
    }

    public static String getCppReferenceTypeName(Class javaClass){
        return String.format("CPP%s", javaClass.getSimpleName());
    }
}