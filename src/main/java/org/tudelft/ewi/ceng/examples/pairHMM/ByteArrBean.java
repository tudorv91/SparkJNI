package org.tudelft.ewi.ceng.examples.pairHMM;

import org.tudelft.ewi.ceng.Bean;
import org.tudelft.ewi.ceng.annotations.JNI_class;
import org.tudelft.ewi.ceng.annotations.JNI_field;
import org.tudelft.ewi.ceng.annotations.JNI_method;
import org.tudelft.ewi.ceng.annotations.JNI_param;

import java.io.Serializable;

/**
 * Created by root on 8/19/16.
 */
@JNI_class public class ByteArrBean extends Bean implements Serializable {
    @JNI_field byte[] arr;

    public ByteArrBean(){}

    @JNI_method public ByteArrBean(@JNI_param(target =  "arr") byte[] arr) {
        this.arr = arr;
    }
}
