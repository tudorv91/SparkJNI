package org.heterojni.examples.pairHMM;

import org.heterojni.sparkjni.annotations.JNI_field;
import org.heterojni.sparkjni.annotations.JNI_method;
import org.heterojni.sparkjni.annotations.JNI_param;
import org.heterojni.sparkjni.annotations.JNI_class;
import org.heterojni.sparkjni.utils.JavaBean;

/**
 * Created by root on 8/29/16.
 */
@JNI_class
public class ImageBean extends JavaBean {
    @JNI_field
    byte[] imageArray;
    @JNI_field byte[] convolutionKernel;

    @JNI_method
    public ImageBean(@JNI_param(target = "imageArray") byte[] imageArray,
                     @JNI_param(target = "convolutionKernel") byte[] convolutionKernel) {
        this.imageArray = imageArray;
        this.convolutionKernel = convolutionKernel;
    }

    public ImageBean(){}
}
