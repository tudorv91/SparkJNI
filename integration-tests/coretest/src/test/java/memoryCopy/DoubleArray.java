package memoryCopy;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.annotations.JNI_class;
import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.annotations.JNI_method;
import sparkjni.utils.annotations.JNI_param;

import java.util.Arrays;

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
