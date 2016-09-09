package org.tudelft.ewi.ceng.sparkjni.jniFunctions;

import org.apache.spark.api.java.function.Function2;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

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

        System.out.println(String.format(JniUtils.INFO_CALLING_REDUCE_METHOD, nativeFunctionName));

        if(nativeMethod == null)
        {
            String errStr = String.format(JniUtils.ERR_COULD_NOT_FIND_METHOD, nativeFunctionName, this.getClass().getName());
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }

        retObj = invokeNativeReduce(o1, o2);
        ((Bean)retObj).setStartRun(startProc);
        ((Bean)retObj).setEndRun(System.nanoTime());
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
