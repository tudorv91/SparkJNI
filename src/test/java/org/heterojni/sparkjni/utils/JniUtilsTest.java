package org.heterojni.sparkjni.utils;

import org.heterojni.TestUtils;
import org.heterojni.examples.vectorOps.VectorBean;
import org.heterojni.examples.vectorOps.VectorMulJni;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkContainers.JniHeader;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.heterojni.sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import org.junit.*;
import org.heterojni.examples.vectorOps.VectorAddJni;

import java.util.List;

import static org.junit.Assert.*;

public class JniUtilsTest{
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "org.heterojni.examples.vectorOps.VectorAddJni";
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI = "org.heterojni.examples.vectorOps.VectorMulJni";
    public static final String CORRECT_OUTPUT_JNI_TEST = "org.heterojni.examples.vectorOps.VectorMulJniJava_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul: mapVectorMul\n" +
            "org.heterojni.examples.vectorOps.VectorAddJniJava_org_heterojni_examples_vectorOps_VectorAddJni_reduceVectorAdd: reduceVectorAdd\n";
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