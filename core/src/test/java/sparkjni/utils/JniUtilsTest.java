package sparkjni.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import testutils.TestUtils;
import unitTestUtils.VectorAddJni;
import unitTestUtils.VectorBean;
import unitTestUtils.VectorMulJni;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JniUtilsTest{
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "unitTestUtils.VectorAddJni";
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI = "unitTestUtils.VectorMulJni";
    public static final String CORRECT_OUTPUT_JNI_TEST = "unitTestUtils.VectorMulJniJava_unitTestUtils_VectorMulJni_mapVectorMul: mapVectorMul\n" +
            "unitTestUtils.VectorAddJniJava_unitTestUtils_VectorAddJni_reduceVectorAdd: reduceVectorAdd\n";
    static TestUtils testUtils;
    private static SparkJni sparkJni;

    @Before
    public void init(){
        testUtils = new TestUtils(JniUtilsTest.class);
        testUtils.initTestDir();
        sparkJni = testUtils.getSparkJni()
                .registerJniFunction(VectorAddJni.class)
                .registerJniFunction(VectorMulJni.class)
                .registerContainer(VectorBean.class);

        JniLinkHandler.getJniLinkHandlerSingleton().generateCppBeanClasses();
        JniLinkHandler.getJniLinkHandlerSingleton().javah(JniUtils.getClasspath());
    }

    @Test
    public void jniDirAccessorTest(){
        List<JniHeader> headerList = getJniHeaders();
        assertEquals(headerList.size(), 2);
        assertEquals(FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI, headerList.get(0).fullyQualifiedJavaClassName());
        assertEquals(FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI, headerList.get(1).fullyQualifiedJavaClassName());
    }

    private List<JniHeader> getJniHeaders(){
        JniRootContainer dirAccessor = ImmutableJniRootContainerProvider.builder().build()
                .buildJniRootContainer(testUtils.fullPath, testUtils.appName);
        return dirAccessor.jniHeaders();
    }

    @Test
    public void fullNativeHeaderTest(){
        StringBuilder sb = new StringBuilder();
        for(JniHeader header: getJniHeaders()) {
            sb.append(header.fullyQualifiedJavaClassName());
            for(FunctionSignatureMapper jniFunc: header.jniFunctions())
                sb.append(String.format("%s: %s\n",
                        jniFunc.functionNameMapper().jniName(),
                        jniFunc.functionNameMapper().javaName()));
        }
        assertEquals(CORRECT_OUTPUT_JNI_TEST, sb.toString());
    }

    @After
    public void clean(){
        testUtils.cleanTestDir();
    }
}