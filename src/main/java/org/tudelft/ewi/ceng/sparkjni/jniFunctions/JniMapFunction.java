package org.tudelft.ewi.ceng.sparkjni.jniFunctions;

import org.apache.spark.api.java.function.Function;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by root on 9/9/16.
 */
public class JniMapFunction<T1, R> extends JniFunction implements Function<T1, R> {
    private Method nativeMethod = null;

    public JniMapFunction() {
    }

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

        retObj = invokeNativeMap(o);
        System.out.println(String.format(JniUtils.INFO_CALLING_MAP_FUNCTION, nativeFunctionName));

        if(nativeMethod == null)
        {
            String errStr = String.format(JniUtils.ERR_COULD_NOT_FIND_METHOD, nativeFunctionName, this.getClass().getName());
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }

        ((Bean)retObj).setStartRun(startProc);
        ((Bean)retObj).setEndRun(System.nanoTime());
        return retObj;
    }

    private R invokeNativeMap(T1 o){
        try {
            return (R) nativeMethod.invoke(this, o);
        } catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}
