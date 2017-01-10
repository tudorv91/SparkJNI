package memoryCopy;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

import java.util.Arrays;

/**
 * Created by tudor on 11/19/16.
 */
@JNI_class
public class DoubleArray extends JavaBean{
    @JNI_field(critical = true) double[] dblArr;

    public DoubleArray(){}

    @JNI_method public DoubleArray(@JNI_param(target = "dblArr") double[] dblArr) {
        this.dblArr = dblArr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleArray that = (DoubleArray) o;

        return Arrays.equals(dblArr, that.dblArr);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dblArr);
    }
}
