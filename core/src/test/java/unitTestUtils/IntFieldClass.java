package unitTestUtils;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.annotations.JNI_method;
import sparkjni.utils.annotations.JNI_param;

/**
 * Created by Tudor on 9/5/16.
 */
public class IntFieldClass extends JavaBean {
    @JNI_field
    int anInt;
    public IntFieldClass() {}
    @JNI_method
    public IntFieldClass(@JNI_param (target = "anInt") int anInt) {}
}
