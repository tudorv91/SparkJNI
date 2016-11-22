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
package sparkjni.utils.cpp.methods;

import sparkjni.dataLink.CppBean;

/**
 * Created by Tudor on 8/6/16.
 */
public abstract class NativeMethod {
    CppBean ownerClass;
    String returnType;
    String methodBody;
    String methodName;
    String ownerClassName;
    String accessor;
    boolean isValid = true;

    public NativeMethod(CppBean cppBean){
        ownerClass = cppBean;
        ownerClassName = cppBean.getCppClassName();
    }

    protected NativeMethod() {
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public String getReturnType(){
        return returnType;
    }
    public String getMethodBody(){
        return methodBody;
    }
    public String getAccessor(){
        return accessor;
    }
    public String getMethodName() {
        return methodName;
    }
}
