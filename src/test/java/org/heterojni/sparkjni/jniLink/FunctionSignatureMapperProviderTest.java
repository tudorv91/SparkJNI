package org.heterojni.sparkjni.jniLink;

import org.heterojni.TestUtils;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import org.heterojni.sparkjni.jniLink.linkHandlers.FunctionSignatureMapperProvider;
import org.heterojni.sparkjni.jniLink.linkHandlers.JniHeaderHandler;
import org.heterojni.sparkjni.jniFunctions.JniMapFunction;
import org.heterojni.sparkjni.jniFunctions.JniReduceFunction;
import org.heterojni.sparkjni.jniLink.linkHandlers.JniLinkHandler;
import org.heterojni.sparkjni.utils.JniDirAccessor;
import org.heterojni.sparkjni.utils.MetadataHandler;
import org.junit.*;
import org.heterojni.examples.vectorOps.VectorBean;
import org.heterojni.sparkjni.annotations.JNI_functionClass;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.SparkJni;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    FunctionSignatureMapperProvider prototype;
    TestUtils testUtils;
    JniDirAccessor jniDirAccessor;
    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils(FunctionSignatureMapperProvider.class);
        testUtils.initTestDir();
        SparkJni.reset();
        SparkJni.setNativePath(testUtils.defaultTestFolder);
        SparkJni.registerContainer(VectorBean.class);
        SparkJni.registerJniFunction(TestMapperJniFunc.class);
        SparkJni.registerJniFunction(TestReduceJniFunc.class);
        JniLinkHandler.getJniLinkHandlerSingleton().generateCppBeanClasses();
        SparkJni.setJdkPath(testUtils.jdkPath);
        jniDirAccessor = new JniDirAccessor(testUtils.defaultTestFolder);
        SparkJni.getJniHandler().javah(JniUtils.getClasspath());
        SparkJni.getJniHandler().deployLink();
}

    @After
    public void tearDown() throws Exception {
        testUtils.cleanTestDir();
    }

    private JniHeaderHandler getJniHeaderByName(CopyOnWriteArrayList<JniHeaderHandler> jniHeaderHandlers, String name){
        for(JniHeaderHandler jniHeaderHandler : jniHeaderHandlers){
            if(jniHeaderHandler.getFullyQualifiedJavaClassName().contains(name))
                return jniHeaderHandler;
        }
        return null;
    }

    @Test
    public void testMapperJniFunctionPrototype() throws Exception {
        JniHeaderHandler mapperHeader = getJniHeaderByName(jniDirAccessor.getJniHeaderHandlers(), "TestMapperJniFunc");
        List<FunctionSignatureMapper> functionSignatureMappers = mapperHeader.getJniFunctions();
        assertEquals(functionSignatureMappers.size(), 1);
        assertNotNull(mapperHeader);

        FunctionSignatureMapper functionSignatureMapper = functionSignatureMappers.get(0);
        assertEquals(functionSignatureMapper.returnTypeMapper().cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().size(), 1);
        assertEquals(functionSignatureMapper.parameterList().get(0).cppType().getCppClassName(), "CPPVectorBean");
    }

    @Test
    public void testReduceJniFunctionPrototype() throws Exception {
        JniHeaderHandler reduceHeader = getJniHeaderByName(jniDirAccessor.getJniHeaderHandlers(), "TestReduceJniFunc");
        assertNotNull(reduceHeader);
        List<FunctionSignatureMapper> functionSignatureMappers = reduceHeader.getJniFunctions();
        assertEquals(functionSignatureMappers.size(), 1);

        FunctionSignatureMapper functionSignatureMapper = functionSignatureMappers.get(0);
        assertEquals(functionSignatureMapper.returnTypeMapper().cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().size(), 2);
        assertEquals(functionSignatureMapper.parameterList().get(0).cppType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionSignatureMapper.parameterList().get(1).cppType().getCppClassName(), "CPPVectorBean");
    }
}