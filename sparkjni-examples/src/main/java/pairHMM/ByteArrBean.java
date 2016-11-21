package pairHMM;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.dataLink.JavaBean;

import java.io.Serializable;

/**
 * Created by Tudor on 8/19/16.
 */
@JNI_class
public class ByteArrBean extends JavaBean implements Serializable {
    @JNI_field
    byte[] arr;

    public ByteArrBean(){}

    @JNI_method
    public ByteArrBean(@JNI_param(target =  "arr") byte[] arr) {
        this.arr = arr;
    }
}
