package sparkjni.utils;

import sparkjni.dataLink.CppBean;
import sparkjni.utils.cpp.fields.CppField;
import org.junit.Before;
import org.junit.Test;
import unitTestUtils.BeanClass;

import static org.junit.Assert.assertEquals;

/**
 * Created by Tudor on 9/21/16.
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