package generator.packageOne;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

import java.util.Arrays;

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

        return Arrays.equals(data, that.data);}
}