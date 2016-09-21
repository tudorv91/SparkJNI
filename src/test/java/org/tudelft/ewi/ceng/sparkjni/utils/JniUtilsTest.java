package org.tudelft.ewi.ceng.sparkjni.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tudelft.ewi.ceng.TestUtils;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;
import org.tudelft.ewi.ceng.sparkjni.jniFunctions.JniMapFunction;
import org.tudelft.ewi.ceng.sparkjni.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by root on 9/5/16.
 */
class MapperTester<T1, R> extends JniMapFunction<T1, R>{
    public MapperTester() {
        super();
    }

    public MapperTester(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native Integer testMapperNo1(Integer someInt);
    public native Integer testMapperNo2(Integer someInt);
}

public class JniUtilsTest{
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI = "org.tudelft.ewi.ceng.examples.vectorOps.VectorAddJni";
    public static final String FULLY_QUALIFIED_NAME_TEST_HEADE_MULJNI = "org.tudelft.ewi.ceng.examples.vectorOps.VectorMulJni";
    public static final String CORRECT_OUTPUT_JNI_TEST = "org.tudelft.ewi.ceng.examples.vectorOps.VectorAddJni" +
            "Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorAddJni_reduceVectorAdd: reduceVectorAdd" +
            "org.tudelft.ewi.ceng.examples.vectorOps.VectorMulJni" +
            "Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorMulJni_mapVectorMul: mapVectorMul";
    TestUtils utils;

    @Before
    public void init(){
        utils = new TestUtils("testApp");
        utils.initTestDir();
    }

    @Test
    public void jniDirAccessorTest(){
        JniDirAccessor jniDirAccessor = new JniDirAccessor(utils.fullPath);
        List<JniHeader> headerList = jniDirAccessor.getJniHeaders();

        assertEquals(headerList.size(), 2);
        assertEquals(headerList.get(0).getFullyQualifiedJavaClassName(), FULLY_QUALIFIED_NAME_TEST_HEADER_ADDJNI);
        assertEquals(headerList.get(1).getFullyQualifiedJavaClassName(), FULLY_QUALIFIED_NAME_TEST_HEADE_MULJNI);
    }

    @Test
    public void fullNativeHeaderTest(){
        StringBuilder sb = new StringBuilder();
        JniDirAccessor dirAccessor = new JniDirAccessor(utils.fullPath);
        List<JniHeader> headers = dirAccessor.getJniHeaders();
        for(JniHeader header: headers) {
            sb.append(header.getFullyQualifiedJavaClassName());
            for(JniFunctionPrototype jniFunc: header.getJniFunctions())
                sb.append(String.format("%s: %s", jniFunc.getJniFuncName(), jniFunc.getDefiningJavaMethodName()));
        }
        assertEquals(sb.toString(), CORRECT_OUTPUT_JNI_TEST);
    }

    @After
    public void clean(){
        utils.cleanTestDir();
    }
}