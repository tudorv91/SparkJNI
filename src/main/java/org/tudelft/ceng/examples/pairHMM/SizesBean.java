package org.tudelft.ceng.examples.pairHMM;

import org.tudelft.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ceng.sparkjni.utils.JavaBean;
import org.tudelft.ceng.sparkjni.annotations.JNI_method;

import java.io.Serializable;

/**
 * Created by root on 8/16/16.
 */
@JNI_class
public class SizesBean extends JavaBean implements Serializable {
    @JNI_field
    int[] col1 = {0};
    @JNI_field int[] col2 = {0};

    @JNI_method public SizesBean(@JNI_param(target = "col1") int[] col1, @JNI_param(target = "col2") int[] col2) {
        this.col1 = col1;
        this.col2 = col2;
    }

    public SizesBean(){}
}
