package org.heterojni.examples.vectorOps;

import org.heterojni.sparkjni.annotations.JNI_field;
import org.heterojni.sparkjni.annotations.JNI_method;
import org.heterojni.sparkjni.annotations.JNI_param;
import org.heterojni.sparkjni.annotations.JNI_class;
import org.heterojni.sparkjni.utils.JavaBean;

import java.util.Arrays;

/**
 * Created by root on 9/8/16.
 */
@JNI_class
public class VectorBean extends JavaBean {
    @JNI_field
    int[] data;

    @JNI_method
    public VectorBean(@JNI_param(target = "data") int[] data) {
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
