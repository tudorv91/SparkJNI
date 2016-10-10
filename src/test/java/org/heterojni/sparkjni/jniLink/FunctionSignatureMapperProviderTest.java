package org.heterojni.sparkjni.jniLink;

import org.heterojni.TestUtils;
import org.heterojni.examples.vectorOps.VectorBean;
import org.heterojni.sparkjni.jniLink.linkHandlers.FunctionSignatureMapperProvider;
import org.heterojni.sparkjni.utils.jniAnnotations.JNI_functionClass;
import org.heterojni.sparkjni.jniLink.jniFunctions.JniMapFunction;
import org.heterojni.sparkjni.jniLink.jniFunctions.JniReduceFunction;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkContainers.JniHeader;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.heterojni.sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import org.heterojni.sparkjni.utils.JniLinkHandler;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.SparkJni;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by root on 9/24/16.
 */

@JNI_functionClass class TestMapperJniFunc extends JniMapFunction {
    public TestMapperJniFunc() {
    }

    public TestMapperJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doMapTest(VectorBean vectorBean);
}

@JNI_functionClass class TestReduceJniFunc extends JniReduceFunction {
    public TestReduceJniFunc() {
    }

    public TestReduceJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doReduceTest(VectorBean v1, VectorBean v2);
}

public class FunctionSignatureMapperProviderTest {
    static TestUtils testUtils;
    static JniRootContainer jniRootContainer;
    static SparkJni sparkJni;
    @BeforeClass
    public static void setUp() throws Exception {
        testUtils = new TestUtils(FunctionSignatureMapperProvider.class);
        testUtils.initTestDir();

        sparkJni = testUtils.getSparkJni()
                .registerContainer(VectorBean.class)
                .registerJniFunction(TestMapperJniFunc.class)
                .registerJniFunction(TestReduceJniFunc.class);

        sparkJni.getJniHandler().generateCppBeanClasses();
        sparkJni.getJniHandler().javah(JniUtils.getClasspath());
        jniRootContainer = ImmutableJniRootContainerProvider.builder().build()
                .buildJniRootContainer(testUtils.defaultTestFolder, testUtils.appName);
        sparkJni.getJniHandler().deployLink();
    }

    @After
    public void tearDown() throws Exception {
        testUtils.cleanTestDir();
    }

    private JniHeader getJniHeaderByName(List<JniHeader> jniHeaders, String name){
        for(JniHeader jniHeader : jniHeaders){
            if(jniHeader.fullyQualifiedJavaClassName().contains(name))
                return jniHeader;
        }
        return null;
    }

    @Test
    public void testMapperJniFunctionPrototype() throws Exception {
        JniHeader mapperHeader = getJniHeaderByName(jniRootContainer.jniHeaders(), "TestMapperJniFunc");
        List<FunctionSignatureMapper> functionSignatureMappers = mapperHeader.jniFunctions();
        assertEquals(functionSignatureMappers.size(), 1);
        assertNotNull(mapperHeader);

        FunctionSignatureMapper functionSignatureMapper = functionSignatureMappers.get(0);
        assertEquals(functionSignatureMapper.returnTypeMapper().cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().size(), 1);
        assertEquals(functionSignatureMapper.parameterList().get(0).cppType().getCppClassName(), "CPPVectorBean");
    }

    @Test
    public void testReduceJniFunctionPrototype() throws Exception {
        JniHeader reduceHeader = getJniHeaderByName(jniRootContainer.jniHeaders(), "TestReduceJniFunc");
        assertNotNull(reduceHeader);
        List<FunctionSignatureMapper> functionSignatureMappers = reduceHeader.jniFunctions();
        assertEquals(functionSignatureMappers.size(), 1);

        FunctionSignatureMapper functionSignatureMapper = functionSignatureMappers.get(0);
        assertEquals(functionSignatureMapper.returnTypeMapper().cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().size(), 2);
        assertEquals(functionSignatureMapper.parameterList().get(0).cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().get(1).cppType().getCppClassName(), "CPPVectorBean");
    }
}