package org.tudelft.ewi.ceng.examples.vectorOps;

import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;

/**
 * Created by root on 9/8/16.
 */
@JNI_class public class VectorBean extends Bean {
    @JNI_field int[] data;

    @JNI_method public VectorBean(@JNI_param(target = "data") int[] data) {
        this.data = data;
    }
}
