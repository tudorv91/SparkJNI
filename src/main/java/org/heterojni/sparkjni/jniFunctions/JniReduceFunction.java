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
package org.heterojni.sparkjni.jniFunctions;

import org.apache.spark.api.java.function.Function2;
import org.heterojni.sparkjni.exceptions.HardSparkJniException;
import org.heterojni.sparkjni.exceptions.Messages;
import org.heterojni.sparkjni.utils.JavaBean;

/**
 * Created by root on 9/9/16.
 */
public class JniReduceFunction<T1, T2, R> extends JniFunction implements Function2<T1, T2, R> {
    public JniReduceFunction() {
    }

    public JniReduceFunction(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    @Override
    public R call(T1 o1, T2 o2) {
        long startProc = System.nanoTime();
        loadNativeLib();
        R retObj = null;
        nativeMethod = getNativeMethodByName(this.getClass(), nativeFunctionName);
        retObj = invokeNativeReduce(o1, o2);
        ((JavaBean)retObj).setStartRun(startProc);
        ((JavaBean)retObj).setEndRun(System.nanoTime());
        return retObj;
    }

    private R invokeNativeReduce(T1 o1, T2 o2){
        if(nativeMethod == null)
        {
            String errStr = String.format(Messages.ERR_COULD_NOT_FIND_METHOD, nativeFunctionName, this.getClass().getName());
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }
        System.out.println(String.format(Messages.INFO_CALLING_REDUCE_METHOD, nativeFunctionName));
        try {
            R retObj = (R) nativeMethod.invoke(this, o1, o2);
            if(retObj == null)
                throw new HardSparkJniException(String.format("Object returned from native method %s is NULL", nativeFunctionName));
            return retObj;
        } catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}
