package org.heterojni.examples.sparkJNIPi;

import org.heterojni.sparkjni.dataLink.JavaBean;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_field;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_method;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_param;

public class SumArray extends JavaBean{
    @JNI_field
    public int sum[];

    public SumArray() {
    }

    @JNI_method
    public SumArray(@JNI_param(target = "sum") int[] sum) {
        this.sum = sum;
    }
}
