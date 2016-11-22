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
package sparkjni.utils.exceptions;

public class Messages {
    public static final String INFO_CALLING_REDUCE_METHOD = "Calling method %s";
    public static final String INFO_CALLING_MAP_FUNCTION = "Calling method %s";
    public static final String ERR_COULD_NOT_FIND_METHOD = "Could not find method %s in class %s";
    // Messages
    public static final String ERR_JAVAH_FAILED = "[ERROR] javah failed!";
    public static final String NATIVE_PATH_NOT_SET = "[ERROR]Please set native path with SparkJni.setNativePath(String path). Exiting..";
    public static final String NATIVE_PATH_ERROR = "[ERROR]Specified native path does not exist or is not a valid directory. Exiting..";
    public static final String MAKEFILE_GENERATION_FAILED_ERROR = "[ERROR]Makefile generation failed. Exiting..";
    public static final String ERR_CPP_FILE_GENERATION_FAILED = "Cpp File generation failed";
    public static final String ERR_NO_JNI_PROTOTYPES_FOUND_IN_USER_DIR = "No JNI prototypes found in user dir..";
    public static final String ERR_SPARK_CONTEXT_IS_NULL_EXITING = "Spark context is null. Exiting..";
    public static final String ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE = "Invalid formatting for file %s at line \n\"%s\"";
    public static final String ERR_JNI_FUNCTION_CLASS_DOES_NOT_INHERIT_JNI_FUNCTION = "User-defined JNI function class does not inherit JniFunction";
    public static final String KERNEL_MISSING_NOTICE = "[INFO]Please provide a kernel file";
    public static final String ERR_KERNEL_FILE_GENERATION_FAILED = "Kernel wrapper header file generation failed..";
    public static final String ERR_PROVIDED_JCLASS_NULL_STR = "Provided java class object is null..!";
    public static final String ERR_CONSTRUCTOR_OBJECT_METHOD_IS_NULL_STR = "Constructor object method is null";
    public static final String ERR_CPP_BUILD_FAILED = "Run process command failed!";
    public static final String ERR_PLEASE_DO_SET_THE_JDK_PATH = "Please do set the JDK PATH programatically or as a system-wide environment variable" +
            "in /etc/profile.d..";
    public static final String ERR_FIELD_WITH_TYPE_IS_NOT_AN_ARRAY = "Field %s with type %s is not an array..";
    public static final String ERR_CLASS_NOT_FOUND = "Class not found %s";
}
