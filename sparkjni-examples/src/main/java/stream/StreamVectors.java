package stream;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

import java.util.Arrays;

/**
 * Created by tudor on 12/4/16.
 */
public class StreamVectors extends JavaBean{
    @JNI_field double A[];
    @JNI_field double B[];

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamVectors that = (StreamVectors) o;

        if (Double.compare(that.scaling_constant, scaling_constant) != 0) return false;
        if (!Arrays.equals(A, that.A)) return false;
        if (!Arrays.equals(B, that.B)) return false;
        return Arrays.equals(C, that.C);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Arrays.hashCode(A);
        result = 31 * result + Arrays.hashCode(B);
        result = 31 * result + Arrays.hashCode(C);
        temp = Double.doubleToLongBits(scaling_constant);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @JNI_field double C[];
    @JNI_field double scaling_constant;

    @JNI_method public StreamVectors(@JNI_param(target = "A") double[] a, @JNI_param(target = "B") double[] b, @JNI_param(target = "C") double[] c,
                                     @JNI_param(target = "scaling_constant") double scaling_constant) {
        A = a;
        B = b;
        C = c;
        this.scaling_constant = scaling_constant;
    }

    public StreamVectors() {}
}
