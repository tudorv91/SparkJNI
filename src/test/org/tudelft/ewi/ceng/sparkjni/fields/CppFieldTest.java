package org.tudelft.ewi.ceng.sparkjni.fields;

import org.junit.Test;
import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by root on 9/5/16.
 */
class IntFieldClass{
    int anInt;
}

public class CppFieldTest {
    public static ArrayList<CppField> getCppFields(Class testClass){
        CppClass cppClass = new CppClass(testClass);
        return cppClass.getCppFields();
    }
    @Test
    public void testCorrectNumberOfFields(){
        int numberOfFieldsInSimpleClass = getCppFields(IntFieldClass.class).size();
        assertEquals(numberOfFieldsInSimpleClass, 1);
    }

    @Test
    public void testFieldImpl(){
        String declaration = getCppFields(IntFieldClass.class).get(0).fieldDeclaration;

    }
}