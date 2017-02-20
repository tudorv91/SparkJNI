package unitTestUtils;

import sparkjni.dataLink.JavaBean;
import sparkjni.utils.annotations.JNI_field;
import sparkjni.utils.annotations.JNI_method;
import sparkjni.utils.annotations.JNI_param;


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
