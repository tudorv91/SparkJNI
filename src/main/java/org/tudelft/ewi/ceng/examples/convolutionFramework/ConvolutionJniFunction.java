package org.tudelft.ewi.ceng.examples.convolutionFramework;

import org.tudelft.ewi.ceng.JniFunction;
import org.tudelft.ewi.ceng.annotations.JNI_functionClass;

/**
 * Created by root on 7/20/16.
 */
@JNI_functionClass public class ConvolutionJniFunction<T1, R> extends JniFunction<T1, R>{
    public ConvolutionJniFunction(){}
    public ConvolutionJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native CustomImage callNativeConvolution(CustomImage arg);
}
