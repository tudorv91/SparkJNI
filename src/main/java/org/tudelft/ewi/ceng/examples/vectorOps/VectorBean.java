package org.tudelft.ewi.ceng.examples.vectorOps;

import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;

import java.util.Arrays;

/**
 * Created by root on 9/8/16.
 */
@JNI_class public class VectorBean extends Bean {
    @JNI_field int[] data;

    @JNI_method public VectorBean(@JNI_param(target = "data") int[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VectorBean that = (VectorBean) o;

        return Arrays.equals(data, that.data);

    }
}
