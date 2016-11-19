package sparkJNIPi;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

public class RandNumArray extends JavaBean{
    @JNI_field float[] randNumArray;
    public RandNumArray() {}

    @JNI_method public RandNumArray(@JNI_param(target = "randNumArray") float[] randNumArray) {
        this.randNumArray = randNumArray;
    }
}