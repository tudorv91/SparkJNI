package org.tudelft.ewi.ceng.examples.memcpyFramework;

import org.tudelft.ewi.ceng.JniFunction;
import org.tudelft.ewi.ceng.annotations.JNI_functionClass;

/**
 * Created by root on 7/20/16.
 */
@JNI_functionClass public class MemcpyJniFunction<T1, R> extends JniFunction<T1, R>{
    public MemcpyJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public MemcpyJniFunction(){super();}

    public native RawArrBean callNativeMemcpy(RawArrBean arg);
}
