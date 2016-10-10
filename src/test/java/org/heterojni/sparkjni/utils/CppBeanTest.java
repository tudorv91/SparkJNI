package org.heterojni.sparkjni.utils;

import org.heterojni.sparkjni.utils.jniAnnotations.JNI_field;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_method;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_param;
import org.heterojni.sparkjni.dataLink.CppBean;
import org.heterojni.sparkjni.dataLink.JavaBean;
import org.heterojni.sparkjni.utils.cpp.fields.CppField;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

class BeanClass extends JavaBean {
    @JNI_field
    int someInt;
    @JNI_field double[] dblArr;

    public BeanClass(){}
    @JNI_method
    public BeanClass(@JNI_param(target = "someInt") int someInt, @JNI_param(target = "dblArr") double[] dblArr) {
        this.someInt = someInt;
        this.dblArr = dblArr;
    }
}

/**
 * Created by root on 9/21/16.
 */
public class CppBeanTest {
    CppBean classUT;

    @Before
    public void init(){
        classUT = new CppBean(BeanClass.class, "some");
    }

    @Test
    public void getCppFieldByName() throws Exception {
        CppField dblArr = classUT.getCppFieldByName("dblArr");
        CppField someInt = classUT.getCppFieldByName("someInt");
        assertEquals(dblArr.getTypeSignature(), "[D");
        assertEquals(someInt.getTypeSignature(), "I");
    }
}