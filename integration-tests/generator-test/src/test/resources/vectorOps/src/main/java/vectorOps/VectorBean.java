package vectorOps;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.annotations.JNI_class;
import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.annotations.JNI_method;
import sparkjni.utils.annotations.JNI_param;

import java.util.Arrays;

/**
 * SparkJNI bean classes are meant to encapsulate contents of RDD elements.
 * These classes should parameterize RDDs and should be annotated with @JNI_class.
 */

@JNI_class
public class VectorBean extends JavaBean {
    /**
     *  Each field that the developer wants accessible in native code should be annotated with @JNI_field.
     */

    @JNI_field
    int[] data;

    /**
     * Public constructors with setter parameters for all native-accessible fields need to be implemented.
     * These constructors are registered with @JNI_method and only one constructor should be annotated per class.
     * Each constructor parameter should be annotated with @JNI_param and a target field that maps constructor parameters to class members.
     */
    @JNI_method
    public VectorBean(@JNI_param (target = "data") int[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VectorBean that = (VectorBean) o;

        return Arrays.equals(data, that.data);}
}