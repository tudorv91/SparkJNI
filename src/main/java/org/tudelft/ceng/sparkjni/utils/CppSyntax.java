package org.tudelft.ceng.sparkjni.utils;

/**
 * Created by root on 9/24/16.
 */
public class CppSyntax {
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
    public static final String JNI_METHOD_SIGNATURE_STR = "(%s)%s";
    public static final String ENV_GET_ARRAY_LENGTH_STR = "\t%s = env->GetArrayLength(%s);\n";
    public static final String SET_S_ARRAY_REGION_STR = "Set%sArrayRegion";
    public static final String CALL_METHOD_4ARGS_STR = "\t%s->%s(%s, %s, %s, %s);\n";
    public static final String NEW_S_ARRAY_STR = "New%sArray";
    public static final String REINTERPRET_OBJ_CAST_STR = "\t%s = reinterpret_cast<%s>(%s_obj);\n";
    public static final String GET_ARRAY_ELEMENTS_STR = "\t%s = %senv->Get%sArrayElements(%s, NULL);\n";
    public static final String CLASS_REF_ASSIGN_STR = "\n\tjniJavaClassRef = replaceMeObjectName;\n";
    public static final String GET_FIELD_STMT_STR = " = %s->Get%sField(%s, j_%s);\n";
    public static final String GET_FIELD_ID_STMT_STR = "\tjfieldID j_%s = env->GetFieldID(%s, \"%s\", \"%s\");\n\t";
    public static final String NATIVE_TARGET_TYPE_JAVA_DEFINED = "javaDefined";
    public static final String CONSTRUCTOR_PARAM_DEFAULT_NAME_MAPPING = "javaParamName";
    public static final String JAVAH_SECTION = "javah -classpath %s -d %s %s";
    public static final String KERNEL_PATH_STR = "%s/%s.cpp";
    public static final int PROTOTYPE_WORD_NO_IN_LINE = 3;
    static final String EXEC_MAKE_CLEAN = "make clean -C %s";
    static final String EXEC_MAKE = "make -C %s";
    // Constants
    static final String NEW_MAKEFILE_SECTION = "program_NAME \t\t\t:= %s\n" +
            "program_C_SRCS\t\t\t:= $(wildcard *.c)\n" +
            "program_CPP_SRCS\t\t:= $(wildcard *.cpp)\n" +
            "program_C_OBJS \t\t\t:= ${program_C_SRCS:.c=.o}\n" +
            "JAVA_JDK\t\t\t\t:= %s\n" +
            "program_INCLUDE_DIRS \t:= $(JAVA_JDK)/include $(JAVA_JDK)/include/linux %s\n" +
            "program_LIBRARY_DIRS \t:= %s\n" +
            "program_LIBRARIES \t\t:=  %s\n" +
            "program_STATIC_LIBS\t\t:= %s\n" +
            "DEFINES \t\t\t\t:= %s\n" +
            "DEFINES_LINE\t\t\t:= $(addprefix -D, $(DEFINES))\n" +
            "\n" +
            "CFLAGS \t+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) \n" +
            "CFLAGS\t+= -std=c11 -Wall -m64 -lrt -lpthread -fopenmp -fPIC\n" +
            "CPPFLAGS\t+= $(foreach includedir,$(program_INCLUDE_DIRS),-I$(includedir)) \n" +
            "CPPFLAGS\t+= -shared -fPIC -std=c++11 -O3 -m64 -lrt -lpthread -fopenmp\n" +
            "LDFLAGS \t+= $(foreach librarydir,$(program_LIBRARY_DIRS),-L$(librarydir))\n" +
            "LDFLAGS \t+= $(foreach library,$(program_LIBRARIES),-l$(library))\n" +
            "\n" +
            "all: $(program_NAME)\n" +
            "\n" +
            "debug: CFLAGS += -DDEBUG\n" +
            "debug: $(program_C_OBJS)\n" +
            "\tgcc -o $(program_NAME) $(program_C_OBJS) $(program_STATIC_LIBS) $(LDFLAGS) $(CFLAGS)\n" +
            "\n" +
            "$(program_NAME):$(program_C_OBJS)\n" +
            "\tg++ $(program_CPP_SRCS) -o $(program_NAME).so $(DEFINES_LINE) $(program_STATIC_LIBS) $(CPPFLAGS) $(program_C_OBJS) $(LDFLAGS)\n" +
            "\t\n" +
            "clean:\n" +
            "\t@- $(RM) $(program_NAME)\n" +
            "\t@- $(RM) $(program_C_OBJS)\n" +
            "\n" +
            "distclean: clean";
}
