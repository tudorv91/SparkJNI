package org.tudelft.ceng.sparkjni.fields;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.tudelft.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ceng.sparkjni.utils.JavaBean;
import org.tudelft.ceng.sparkjni.utils.CppBean;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Created by root on 9/5/16.
 */
class IntFieldClass extends JavaBean {
    @JNI_field int anInt;
    public IntFieldClass() {}
    @JNI_method public IntFieldClass(@JNI_param(target = "anInt") int anInt) {}
}

public class CppFieldTest {
    CppBean cppBean;
    @Mock
    Field doubleField;

    @Before
    public void init(){
        cppBean = new CppBean(IntFieldClass.class);
    }

    @Test
    public void testCorrectNumberOfFields(){
        int numberOfFieldsInSimpleClass = cppBean.getCppFields().size();
        assertEquals(numberOfFieldsInSimpleClass, 1);
    }

    @Test
    public void testFieldImpl(){
        String declaration = cppBean.getCppFields().get(0).fieldDeclaration;
    }
}