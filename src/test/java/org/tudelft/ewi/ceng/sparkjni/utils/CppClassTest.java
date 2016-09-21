package org.tudelft.ewi.ceng.sparkjni.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ewi.ceng.sparkjni.fields.CppField;

import static org.junit.Assert.*;

class BeanClass extends Bean{
    @JNI_field int someInt;
    @JNI_field double[] dblArr;

    public BeanClass(){}
    @JNI_method public BeanClass(@JNI_param(target = "someInt") int someInt, @JNI_param(target = "dblArr") double[] dblArr) {
        this.someInt = someInt;
        this.dblArr = dblArr;
    }
}

/**
 * Created by root on 9/21/16.
 */
public class CppClassTest {
    CppClass classUT;

    @Before
    public void init(){
        classUT = new CppClass(BeanClass.class);
    }

    @Test
    public void getCppFieldByName() throws Exception {
        CppField dblArr = classUT.getCppFieldByName("dblArr");
        CppField someInt = classUT.getCppFieldByName("someInt");
        assertEquals(dblArr.getTypeSignature(), "[D");
        assertEquals(someInt.getTypeSignature(), "I");
    }
}