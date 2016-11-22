package pairHMM;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.dataLink.JavaBean;

/**
 * Created by Tudor on 8/29/16.
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
