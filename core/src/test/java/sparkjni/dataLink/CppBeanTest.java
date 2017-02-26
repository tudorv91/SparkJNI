package sparkjni.dataLink;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sparkjni.utils.cpp.fields.CppField;
import sparkjni.utils.cpp.methods.CppBeanGetterMethod;
import unitTestUtils.VectorBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CppBeanTest {
    private static final String EXPECTED_CLASS_NAME = "CPPVectorBean";
    private static final String NATIVE_PATH = "some/native/path";
    private final String FILE_PATH_FORMATTER = "%s/%s.%s";
    private CppBean testSubject;

    @Before
    public void setUp() {
        testSubject = new CppBean(VectorBean.class, NATIVE_PATH);
    }

    @Test
    public void isSuccessful() throws Exception {
        assertTrue(testSubject.isSuccessful());
    }

    @Test
    public void getCppClassName() throws Exception {
        assertEquals(EXPECTED_CLASS_NAME, testSubject.getCppClassName());
    }

    @Test
    public void getJavaClass() throws Exception {
        assertEquals(VectorBean.class, testSubject.getJavaClass());
    }

    @Test
    public void getCppFilePath() throws Exception {
        assertEquals(String.format(FILE_PATH_FORMATTER, NATIVE_PATH, EXPECTED_CLASS_NAME, "cpp"), testSubject.getCppFilePath());
    }

    // Method declaration order is sometimes inverted..
    @Ignore
    @Test
    public void getCppImplementation() throws Exception {
        String expectedImplementation = readFileFromResourcesDir(EXPECTED_CLASS_NAME + ".cpp");
        assertEquals(expectedImplementation.replaceAll("\\t", ""), testSubject.getCppImplementation().replaceAll("\\t", ""));
    }

    @Test
    public void getNativePath() throws Exception {
        assertEquals(NATIVE_PATH, testSubject.getNativePath());
    }

    // TODO
    @Ignore
    @Test
    public void getFieldsGettersMap() throws Exception {
        HashMap<CppField, CppBeanGetterMethod> fieldsGettersMap = testSubject.getFieldsGettersMap();
        System.out.print(fieldsGettersMap);
    }

    // TODO
    @Ignore
    @Test
    public void getCppFields() throws Exception {
        List<CppField> cppFields = testSubject.getCppFields();
        System.out.print(cppFields);
    }

    @Test
    public void getHeaderFilePath() throws Exception {
        assertEquals(String.format(FILE_PATH_FORMATTER, NATIVE_PATH, EXPECTED_CLASS_NAME, "h"), testSubject.getHeaderFilePath());
    }

    // Method declaration order is sometimes inverted..
    @Ignore
    @Test
    public void getHeaderImplementation() throws Exception {
        String expectedHeaderImplementation = readFileFromResourcesDir(EXPECTED_CLASS_NAME + ".h");
        assertEquals(expectedHeaderImplementation, testSubject.getHeaderImplementation());
    }

    private static String readFileFromResourcesDir(String fileName) throws IOException {
        String filePath = "vectorBean/" + fileName;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded);
        } catch (IOException e) {
            filePath = "src/test/resources/vectorBean/" + fileName;
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded);
        }
    }
}