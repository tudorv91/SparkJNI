package org.tudelft.ewi.ceng.sparkjni.fields;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ewi.ceng.sparkjni.utils.Bean;
import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by root on 9/5/16.
 */
class IntFieldClass extends Bean{
    @JNI_field int anInt;
    public IntFieldClass() {}
    @JNI_method public IntFieldClass(@JNI_param(target = "anInt") int anInt) {}
}

public class CppFieldTest {
    CppClass cppClass;
    @Mock
    Field doubleField;

    @Before
    public void init(){
        cppClass = new CppClass(IntFieldClass.class);
    }

    @Test
    public void testCorrectNumberOfFields(){
        int numberOfFieldsInSimpleClass = cppClass.getCppFields().size();
        assertEquals(numberOfFieldsInSimpleClass, 1);
    }



    @Test
    public void testFieldImpl(){
        String declaration = cppClass.getCppFields().get(0).fieldDeclaration;
    }
}