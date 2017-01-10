package generator.packageOne;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

@JNI_class
public class AnotherBean extends JavaBean{
    @JNI_field
    int[] someIntArr;

    @JNI_method
    public AnotherBean(@JNI_param(target = "someIntArr") int[] someIntArr) {
        this.someIntArr = someIntArr;
    }

    public AnotherBean() {
    }
}
