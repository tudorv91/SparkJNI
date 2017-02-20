package sparkjni.utils;

import com.google.inject.Guice;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import testutils.TestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JniUtilsTest{
    private static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "unitTestUtils.VectorAddJni";
    private static final String FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI = "unitTestUtils.VectorMulJni";
    private static final String CORRECT_OUTPUT_JNI_TEST = "unitTestUtils.VectorMulJniJava_unitTestUtils_VectorMulJni_mapVectorMul: mapVectorMul\n" +
            "unitTestUtils.VectorAddJniJava_unitTestUtils_VectorAddJni_reduceVectorAdd: reduceVectorAdd\n";
    private static TestUtils testUtils;

    @Before
    public void init(){
        testUtils = new TestUtils(JniUtilsTest.class);
        testUtils.initTestDir();
        JniLinkHandler jniLinkHandler = Guice.createInjector(new AppInjector()).getInstance(JniLinkHandler.class);
        jniLinkHandler.generateCppBeanClasses();
        jniLinkHandler.javah(JniUtils.getClasspath());
    }

    // Depends on full initialization
    @Ignore
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