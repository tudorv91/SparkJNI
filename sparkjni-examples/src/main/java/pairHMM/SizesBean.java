package pairHMM;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.dataLink.JavaBean;

import java.io.Serializable;

/**
 * Created by Tudor on 8/16/16.
 */
@JNI_class
public class SizesBean extends JavaBean implements Serializable {
    @JNI_field(critical = true)
    int[] col1 = {0};
    @JNI_field(critical = true) int[] col2 = {0};

    @JNI_method
    public SizesBean(@JNI_param(target = "col1") int[] col1, @JNI_param(target = "col2") int[] col2) {
        this.col1 = col1;
        this.col2 = col2;
    }

    public SizesBean(){}
}
