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

package sparkjni.utils;//package org.apache.spark.examples;

import sparkjni.dataLink.CppBean;
import sparkjni.utils.exceptions.HardSparkJniException;
import sparkjni.utils.exceptions.Messages;

import javax.annotation.Nonnull;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

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

    public static final String JNI_FALSE = "JNI_FALSE";
    public static final String JNI_TRUE = "JNI_TRUE";

    public static final String JNI_OBJECT = "jobject";
    public static final String JNI_CLASS = "jclass";
    public static final String JNI_STRING = "jstring";
    public static final String JNI_ARRAY = "jarray";

    public static final int DEFAULT_MEMORY_ALIGNMENT = 64;
    public static final String NO_CRITICAL = "NO_CRITICAL";
    public static final String CRITICAL = "CRITICAL";
    public static final boolean PASS_BY_VALUE = false;
    public static final boolean PASS_BY_REFERENCE = true;

    public static String getArrayElementType(Field field) {
        if (!field.getType().isArray())
            throw new IllegalArgumentException(String.format(Messages.ERR_FIELD_WITH_TYPE_IS_NOT_AN_ARRAY,
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
            sb.append(CppSyntax.DEFAULT_INCLUDE_STATEMENTS);
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

    public static boolean isJniNativeFunction(Path path) throws IOException {
        if(!path.toString().endsWith("h"))
            return false;
        for(String line: Files.readAllLines(path, Charset.defaultCharset()))
            if(line.contains("/* Header for class"))
                return true;
        return false;
    }

    public static void checkNativePath(@Nonnull File nativeDir) {
        if (!nativeDir.exists() || !nativeDir.isDirectory())
            throw new RuntimeException(Messages.NATIVE_PATH_ERROR);
    }

    public static String getClasspath(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        return String.format("%s/%s:%s/%s", s, "target/test-classes", s, "target/classes");
    }

    public static String getClasspath(Class specificClass){
        specificClass.getClassLoader().getResource("target");
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        return String.format("%s/%s:%s/%s", s, "target/test-classes", s, "target/classes");
    }

    public static Method getClassMethodyName(Class classForWhichTheDisscussionsAbout, String methodName){
        for(Method clMethod: classForWhichTheDisscussionsAbout.getDeclaredMethods())
            if(clMethod.getName().contains(methodName))
                return clMethod;
        throw new NoSuchMethodError();
    }

    public static String firstLetterCaps(String idStr) {
        return idStr.substring(0, 1).toUpperCase() + idStr.substring(1);
    }

    public static boolean writeFile(String content, String targetfileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetfileName);
            writer.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            writer.close();
        }
        return true;
    }

    public static String generateDefaultHeaderWrapperFileName(String appName, String nativePath){
        return String.format(CppSyntax.KERNEL_WRAPPER_HEADER_PATH, nativePath, appName);
    }

    public static String generateDefaultKernelFileName(String appName, String nativePath){
        return String.format(CppSyntax.KERNEL_PATH_STR, nativePath, appName);
    }

    public static String generateDefaultLibPath(String appName, String nativePath) {
        return String.format(CppSyntax.NATIVE_LIB_PATH, nativePath, appName);
    }

    public static void runProcess(String proc) {
        try {
            Process process = Runtime.getRuntime().exec(proc);
            InputStream errors = process.getErrorStream(),
                    input = process.getInputStream();
            InputStreamReader errorStreamReader = new InputStreamReader(errors),
                    inputStreamReader = new InputStreamReader(input);
            BufferedReader errorBufferedReader = new BufferedReader(errorStreamReader),
                    inputBufferedReader = new BufferedReader(inputStreamReader);
            String line = null;

            while ((line = errorBufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = inputBufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            if (process.waitFor() != 0) {
                throw new HardSparkJniException(String.format("[ERROR] %s:\n\t%s",
                        Messages.ERR_CPP_BUILD_FAILED, proc));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param cppBean The Cpp Bean container.
     * @param prefix of the variable name to be added before the CppBean name. Leave null if not desired.
     * @param idx for uniquely identifying objects of the same type.
     * @return
     */
    public static String generateCppVariableName(CppBean cppBean, String prefix, int idx) {
        String idStr = String.format("%s%d", cppBean.getCppClassName().toLowerCase(), idx);
        prefix = prefix == null ? "" : prefix;
        return prefix.isEmpty() ? idStr : prefix + firstLetterCaps(idStr);
    }

    public static String generateJniPathForClass(Class clazz) {
        return clazz.getCanonicalName().replaceAll(".", "/");
    }

    public static String generateClassNameVariableName(CppBean cppBean, HashSet<String> jClassObjectsSet) {
        String candidateClassObjectName = getClassDefObjectVariableName(cppBean);
        if(jClassObjectsSet != null) {
            if (jClassObjectsSet.contains(candidateClassObjectName))
                return "";
            else {
                jClassObjectsSet.add(candidateClassObjectName);
            }
        }

        return candidateClassObjectName;
    }

    public static String getClassDefObjectVariableName(CppBean cppBean) {
        return String.format(CppSyntax.JNI_CLASSNAME_STR, cppBean.getCppClassName().toLowerCase());
    }

    public static String wrapInSharedPtr(String expression, boolean passByReference){
        return passByReference ? String.format("std::shared_ptr<%s>&", expression) : String.format("std::shared_ptr<%s>", expression);
    }

    public static String makeShared(String cppClassName, String argsList) {
        return String.format("std::make_shared<%s>(%s)", cppClassName, argsList);
    }
}