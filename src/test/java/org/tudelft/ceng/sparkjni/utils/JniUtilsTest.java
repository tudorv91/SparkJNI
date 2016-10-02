package org.tudelft.ceng.sparkjni.utils;

import org.junit.*;
import org.tudelft.ceng.TestUtils;
import org.tudelft.ceng.examples.vectorOps.VectorAddJni;
import org.tudelft.ceng.sparkjni.javaLink.JniFunctionPrototype;
import org.tudelft.ceng.sparkjni.javaLink.JniHeaderHandler;
import org.tudelft.ceng.examples.vectorOps.VectorMulJni;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

public class JniUtilsTest{
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "VectorAddJni";
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADE_MULJNI = "VectorMulJni";
    public static final String CORRECT_OUTPUT_JNI_TEST = "VectorAddJni" +
            "Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorAddJni_reduceVectorAdd: reduceVectorAdd" +
            "VectorMulJni" +
            "Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorMulJni_mapVectorMul: mapVectorMul";
    static TestUtils testUtils;

    @Before
    public void init(){
        testUtils = new TestUtils(JniUtilsTest.class);
        testUtils.initTestDir();
        SparkJni.reset();
        SparkJni.setNativePath(testUtils.fullPath);
        SparkJni.registerJniFunction(VectorAddJni.class);
        SparkJni.registerJniFunction(VectorMulJni.class);
        SparkJni.getJniHandler().javah(JniUtils.getClasspath());
    }

    @Test
    public void jniDirAccessorTest(){
        List<JniHeaderHandler> headerList = getJniHeaders();
        assertEquals(headerList.size(), 2);
        assertEquals(headerList.get(0).getFullyQualifiedJavaClassName(), FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI);
        assertEquals(headerList.get(1).getFullyQualifiedJavaClassName(), FULLY_QUALIFIED_NAME_TEST_HEADE_MULJNI);
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
            for(JniFunctionPrototype jniFunc: header.getJniFunctions())
                sb.append(String.format("%s: %s", jniFunc.getJniFuncName(), jniFunc.getDefiningJavaMethodName()));
        }
        assertEquals(sb.toString(), CORRECT_OUTPUT_JNI_TEST);
    }

    @After
    public void clean(){
        testUtils.cleanTestDir();
    }
}