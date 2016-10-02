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
package org.tudelft.ceng.sparkjni.javaLink;

import org.tudelft.ceng.sparkjni.exceptions.SoftSparkJniException;
import org.tudelft.ceng.sparkjni.exceptions.Messages;
import org.tudelft.ceng.sparkjni.utils.CppBean;
import org.tudelft.ceng.sparkjni.utils.JniUtils;
import org.tudelft.ceng.sparkjni.utils.SparkJni;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class JniFunctionPrototype {
    private String fullyQualifiedJavaClass;
    private String jniFuncName = null;
    private String definingJavaMethodName = null;
    private String parametersLine = null;
    private String jniDefinedReturnType = null;
    private CppBean cppReturnType = null;
    private ArrayList<CppBean> parameterList = new ArrayList<>();
    private int javaFuncParamNo = 0;
    private boolean isStaticMethod;

    public JniFunctionPrototype(String fullyQualifiedJavaClassName, String[] tokens, String parametersLine)
            throws Exception {
        this.fullyQualifiedJavaClass = fullyQualifiedJavaClassName;
        this.parametersLine = parametersLine;
        parse(tokens);
    }

    private void parse(String[] tokens) throws Exception {
        jniFuncName = tokens[tokens.length - 1];
        jniDefinedReturnType = tokens[1];
        String[] aux = jniFuncName.split("_");
        definingJavaMethodName = aux[aux.length - 1];
        String[] params = parametersLine.split(", ");
        javaFuncParamNo = params.length - 2;
        if(javaFuncParamNo < 0) {
            throw new SoftSparkJniException(
                    String.format(Messages.ERR_INVALID_FORMATTING_FOR_FILE_AT_LINE, jniFuncName, parametersLine));
        }
        Class enclosingClass = SparkJni.getJniHandler().getJavaClassByName(fullyQualifiedJavaClass);
        Method jniMethod = JniUtils.getClassMethodyName(enclosingClass, definingJavaMethodName);
        for(Class parameterType: jniMethod.getParameterTypes()){
            parameterList.add(SparkJni.getJniHandler().getContainerByJavaClass(parameterType));
        }
        cppReturnType = SparkJni.getJniHandler().getContainerByJavaClass(jniMethod.getReturnType());
        isStaticMethod = params[1].equals("jclass");
    }

    public String getDefiningJavaMethodName(){
        return definingJavaMethodName;
    }

    public int getJavaFuncParamNo() {
        return javaFuncParamNo;
    }

    public String getFullyQualifiedJavaClass() {
        return fullyQualifiedJavaClass;
    }

    public String getJniFuncName() {
        return jniFuncName;
    }

    public String getJniDefinedReturnType() {
        return jniDefinedReturnType;
    }

    public CppBean getCppReturnType() {
        return cppReturnType;
    }

    public ArrayList<CppBean> getParameterList() {
        return parameterList;
    }

    public boolean isStatic() {
        return isStaticMethod;
    }
}
