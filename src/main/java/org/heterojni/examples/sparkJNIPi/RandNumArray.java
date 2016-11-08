package org.heterojni.examples.sparkJNIPi;

import org.heterojni.sparkjni.dataLink.JavaBean;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_field;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_method;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_param;

public class RandNumArray extends JavaBean{
    @JNI_field float[] randNumArray;
    public RandNumArray() {}

    @JNI_method public RandNumArray(@JNI_param(target = "randNumArray") float[] randNumArray) {
        this.randNumArray = randNumArray;
    }
}