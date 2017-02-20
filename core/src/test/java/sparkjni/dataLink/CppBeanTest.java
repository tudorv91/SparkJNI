package sparkjni.dataLink;

import org.junit.Before;
import org.junit.Test;
import unitTestUtils.VectorBean;

public class CppBeanTest {
    CppBean testSubject;
    private static final String NATIVE_PATH = "some/native/path";

    @Before
    public void setUp() {
        testSubject = new CppBean(VectorBean.class, NATIVE_PATH);
    }

    @Test
    public void getFieldLineImpl() throws Exception {
        String aux = testSubject.getFieldLineImpl();
    }

    @Test
    public void getCppFieldByName() throws Exception {

    }

    @Test
    public void isSuccessful() throws Exception {

    }

    @Test
    public void getCppClassName() throws Exception {

    }

    @Test
    public void getJavaClass() throws Exception {

    }

    @Test
    public void getCppFilePath() throws Exception {

    }

    @Test
    public void getCppImplementation() throws Exception {

    }

    @Test
    public void setNativePath() throws Exception {

    }

    @Test
    public void getNativePath() throws Exception {

    }

    @Test
    public void getFieldsGettersMap() throws Exception {

    }

    @Test
    public void getCppFields() throws Exception {

    }

    @Test
    public void getHeaderFilePath() throws Exception {

    }

    @Test
    public void getHeaderImplementation() throws Exception {

    }

}