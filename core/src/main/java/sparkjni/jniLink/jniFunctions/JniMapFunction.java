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

import org.apache.spark.api.java.function.Function;
import sparkjni.dataLink.JavaBean;
import sparkjni.utils.exceptions.HardSparkJniException;
import sparkjni.utils.exceptions.Messages;

import java.lang.reflect.Method;

public class JniMapFunction<T1, R> extends JniFunction implements Function<T1, R> {
    private Method nativeMethod = null;

    public JniMapFunction() {}

    public JniMapFunction(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    /**
     * @note It is critical to not use generic types defining native methods in inherited user classes.
     * Since we want to avoid runtime C/C++ compilation, we cannot get type information statically
     * @TODO Try find type from .java file context.
     * @param o
     * @return The result of the native method invocation.
     * @throws Exception
     */
    @Override
    public R call(T1 o) {
        loadNativeLib();
        long startProc = System.nanoTime();
        R retObj = null;
        nativeMethod = getNativeMethodByName(this.getClass(), nativeFunctionName);
        retObj = invokeNativeMap(o);
        ((JavaBean)retObj).setStartRun(startProc);
        ((JavaBean)retObj).setEndRun(System.nanoTime());
        return retObj;
    }

    private R invokeNativeMap(T1 o){
        if(nativeMethod == null)
        {
            String errStr = String.format(Messages.ERR_COULD_NOT_FIND_METHOD, nativeFunctionName, this.getClass().getName());
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }
        System.out.println(String.format(Messages.INFO_CALLING_MAP_FUNCTION, nativeFunctionName));
        try {
            R retObj = (R) nativeMethod.invoke(this, o);
            if(retObj == null)
                throw new HardSparkJniException(String.format("Object returned from native method %s is NULL\n" +
                        "You must return a non-null Java object", nativeFunctionName));
            return retObj;
        } catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}
