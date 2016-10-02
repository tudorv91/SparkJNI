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
package org.tudelft.ceng.sparkjni.jniFunctions;

import org.apache.spark.api.java.function.Function2;
import org.tudelft.ceng.sparkjni.exceptions.Messages;
import org.tudelft.ceng.sparkjni.utils.JavaBean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by root on 9/9/16.
 */
public class JniReduceFunction<T1, T2, R> extends JniFunction implements Function2<T1, T2, R> {
    private Method nativeMethod = null;

    public JniReduceFunction() {
    }

    public JniReduceFunction(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    /**
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public R call(T1 o1, T2 o2) {
        long startProc = System.nanoTime();
        R retObj = null;
        Class thisClass = this.getClass();
        Method[] methods = thisClass.getMethods();
        loadNativeLib();

        for(Method method: methods){
            if(Modifier.isNative(method.getModifiers()) && method.getName().equals(nativeFunctionName)){
                nativeMethod = method;
            }
        }

        System.out.println(String.format(Messages.INFO_CALLING_REDUCE_METHOD, nativeFunctionName));

        if(nativeMethod == null)
        {
            String errStr = String.format(Messages.ERR_COULD_NOT_FIND_METHOD, nativeFunctionName, this.getClass().getName());
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }

        retObj = invokeNativeReduce(o1, o2);
        ((JavaBean)retObj).setStartRun(startProc);
        ((JavaBean)retObj).setEndRun(System.nanoTime());
        return retObj;
    }

    private R invokeNativeReduce(T1 o1, T2 o2){
        try {
            return (R) nativeMethod.invoke(this, o1, o2);
        } catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}
