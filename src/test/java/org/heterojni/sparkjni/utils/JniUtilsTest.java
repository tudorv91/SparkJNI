package org.heterojni.sparkjni.utils;

import org.heterojni.TestUtils;
import org.heterojni.examples.vectorOps.VectorBean;
import org.heterojni.examples.vectorOps.VectorMulJni;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkHandlers.JniLinkHandler;
import org.junit.*;
import org.heterojni.examples.vectorOps.VectorAddJni;
import org.heterojni.sparkjni.jniLink.linkHandlers.JniHeaderHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

public class JniUtilsTest{
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "org.heterojni.examples.vectorOps.VectorAddJni";
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI = "org.heterojni.examples.vectorOps.VectorMulJni";
    public static final String CORRECT_OUTPUT_JNI_TEST = "org.heterojni.examples.vectorOps.VectorMulJniJava_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul: mapVectorMul\n" +
            "org.heterojni.examples.vectorOps.VectorAddJniJava_org_heterojni_examples_vectorOps_VectorAddJni_reduceVectorAdd: reduceVectorAdd\n";
    static TestUtils testUtils;

    @BeforeClass
    public static void initBeforeClass(){
        SparkJni.reset();
    }

    @Before
    public void init(){
        testUtils = new TestUtils(JniUtilsTest.class);
        testUtils.initTestDir();
        SparkJni.setNativePath(testUtils.fullPath);
        SparkJni.registerJniFunction(VectorAddJni.class);
        SparkJni.registerJniFunction(VectorMulJni.class);
        SparkJni.registerContainer(VectorBean.class);
        JniLinkHandler.getJniLinkHandlerSingleton().generateCppBeanClasses();
        SparkJni.getJniHandler().javah(JniUtils.getClasspath());
    }

    @Test
    public void jniDirAccessorTest(){
        List<JniHeaderHandler> headerList = getJniHeaders();
        assertEquals(headerList.size(), 2);
        assertEquals(FULLY_QUALIFIED_NAME_TEST_HEADER_MULJNI, headerList.get(0).getFullyQualifiedJavaClassName());
        assertEquals(FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI, headerList.get(1).getFullyQualifiedJavaClassName());
    }

    private CopyOnWriteArrayList<JniHeaderHandler> getJniHeaders(){
        JniDirAccessor dirAccessor = new JniDirAccessor(testUtils.fullPath);
        return dirAccessor.getJniHeaderHandlers();
    }

    @Test
    public void fullNativeHeaderTest(){
        StringBuilder sb = new StringBuilder();
        for(JniHeaderHandler header: getJniHeaders()) {
            sb.append(header.getFullyQualifiedJavaClassName());
            for(FunctionSignatureMapper jniFunc: header.getJniFunctions())
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