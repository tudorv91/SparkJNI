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
package sparkjni.jniLink.jniFunctions;

import org.apache.spark.SparkFiles;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Tudor on 7/20/16.
 * Abstract class to be inherited for all JNI function user implementation classes.
 */
public abstract class JniFunction<T1, R> implements Serializable{
    String nativeLibPath;
    String nativeFunctionName;
    Method nativeMethod = null;

    public JniFunction(){}

    public JniFunction(String nativeLibPath, String nativeFunctionName){
        this.nativeLibPath = nativeLibPath;
        this.nativeFunctionName = nativeFunctionName;
    }

    public ArrayList<Class> getParamTypes(String methodName){
        ArrayList<Class> paramTypes = new ArrayList<>();
        Class callerClass = this.getClass();
        for(Method method: callerClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)){
                for(Class paramType: method.getParameterTypes())
                    paramTypes.add(paramType);
            }
            return paramTypes;
        }

        return null;
    }

    Method getNativeMethodByName(Class<? extends JniReduceFunction> aClass, String nativeFunctionName) {
        Class thisClass = this.getClass();
        Method[] methods = thisClass.getMethods();

        for(Method method: methods){
            if(Modifier.isNative(method.getModifiers()) && method.getName().equals(nativeFunctionName)){
                return method;
            }
        }
        return null;
    }


    void loadNativeLib(){
        if(nativeLibPath.contains("/"))
            System.load(nativeLibPath);
        else
            try{
                System.load(SparkFiles.get(nativeLibPath));
            } catch(Exception e){
                System.load(nativeLibPath);
            }
    }
}