package org.tudelft.ceng.examples.pairHMM;

import org.tudelft.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ceng.sparkjni.utils.JavaBean;
import org.tudelft.ceng.sparkjni.annotations.JNI_method;

/**
 * Created by root on 8/29/16.
 */
@JNI_class
public class ImageBean extends JavaBean {
    @JNI_field
    byte[] imageArray;
    @JNI_field byte[] convolutionKernel;

    @JNI_method public ImageBean(@JNI_param(target = "imageArray") byte[] imageArray,
                                 @JNI_param(target = "convolutionKernel") byte[] convolutionKernel) {
        this.imageArray = imageArray;
        this.convolutionKernel = convolutionKernel;
    }

    public ImageBean(){}
}
