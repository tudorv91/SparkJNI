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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.spark.SparkFiles;
import sparkjni.utils.SparkJni;

import javax.annotation.Nullable;
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

    public JniFunction(){
        this.nativeLibPath = SparkJni.getMetadataHandler().getNativeLibPath();
        this.nativeFunctionName = tryGetUniqueNativeMethod().transform(new Function<Method, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Method method) {
                return method.getName();
            }
        }).orNull();
    }

    public JniFunction(String nativeLibPath, String nativeFunctionName){
        this.nativeLibPath = nativeLibPath;
        this.nativeFunctionName = nativeFunctionName;
    }

    public JniFunction(String nativeFunctionName){
        this();
        this.nativeFunctionName = nativeFunctionName;
    }

    public ArrayList<Class> getParamTypes(String methodName){
        ArrayList<Class> paramTypes = new ArrayList<>();
        Class callerClass = this.getClass();
        for(Method method: callerClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)){
                 Collections.addAll(paramTypes, method.getParameterTypes());
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

    Optional<Method> tryGetUniqueNativeMethod(){
        Class thisClass = this.getClass();
        Method[] methods = thisClass.getMethods();

        for(Method method: methods){
            if(Modifier.isNative(method.getModifiers())){
                return Optional.of(method);
            }
        }
        return Optional.absent();
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