package unitTestUtils;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;

/**
 * Created by tudor on 11/19/16.
 */
public class BeanClass extends JavaBean {
    @JNI_field
    int someInt;
    @JNI_field double[] dblArr;

    public BeanClass(){}
    @JNI_method
    public BeanClass(@JNI_param (target = "someInt") int someInt, @JNI_param(target = "dblArr") double[] dblArr) {
        this.someInt = someInt;
        this.dblArr = dblArr;
    }
}
