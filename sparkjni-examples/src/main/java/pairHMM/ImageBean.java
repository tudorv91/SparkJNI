package pairHMM;

import sparkjni.utils.annotations.JNI_class;
import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.annotations.JNI_method;
import sparkjni.utils.annotations.JNI_param;
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
