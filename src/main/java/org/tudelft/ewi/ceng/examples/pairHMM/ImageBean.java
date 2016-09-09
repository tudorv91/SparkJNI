package org.tudelft.ewi.ceng.examples.pairHMM;

import org.tudelft.ewi.ceng.sparkjni.utils.Bean;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;

/**
 * Created by root on 8/29/16.
 */
@JNI_class public class ImageBean extends Bean {
    @JNI_field byte[] imageArray;
    @JNI_field byte[] convolutionKernel;

    @JNI_method public ImageBean(@JNI_param(target = "imageArray") byte[] imageArray,
                                 @JNI_param(target = "convolutionKernel") byte[] convolutionKernel) {
        this.imageArray = imageArray;
        this.convolutionKernel = convolutionKernel;
    }

    public ImageBean(){}
}
