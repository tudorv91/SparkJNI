package sparkjni.utils.cpp.fields;

import sparkjni.dataLink.CppBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import unitTestUtils.IntFieldClass;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class CppFieldTest {
    CppBean cppBean;
    @Mock
    Field doubleField;

    @Before
    public void init(){
        cppBean = new CppBean(IntFieldClass.class, "someNativePath");
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