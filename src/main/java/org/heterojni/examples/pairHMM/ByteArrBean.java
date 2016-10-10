package org.heterojni.examples.pairHMM;

import org.heterojni.sparkjni.utils.jniAnnotations.JNI_class;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_field;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_method;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_param;
import org.heterojni.sparkjni.dataLink.JavaBean;

import java.io.Serializable;

/**
 * Created by root on 8/19/16.
 */
@JNI_class
public class ByteArrBean extends JavaBean implements Serializable {
    @JNI_field
    byte[] arr;

    public ByteArrBean(){}

    @JNI_method
    public ByteArrBean(@JNI_param(target =  "arr") byte[] arr) {
        this.arr = arr;
    }
}
