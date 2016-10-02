package org.tudelft.ceng.sparkjni.javaLink;

import org.junit.*;
import org.tudelft.ceng.sparkjni.utils.JniDirAccessor;
import org.tudelft.ceng.TestUtils;
import org.tudelft.ceng.examples.vectorOps.VectorBean;
import org.tudelft.ceng.sparkjni.annotations.JNI_functionClass;
import org.tudelft.ceng.sparkjni.jniFunctions.JniMapFunction;
import org.tudelft.ceng.sparkjni.jniFunctions.JniReduceFunction;
import org.tudelft.ceng.sparkjni.utils.JniUtils;
import org.tudelft.ceng.sparkjni.utils.SparkJni;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by root on 9/24/16.
 */

@JNI_functionClass class TestMapperJniFunc extends JniMapFunction{
    public TestMapperJniFunc() {
    }

    public TestMapperJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doMapTest(VectorBean vectorBean);
}

@JNI_functionClass class TestReduceJniFunc extends JniReduceFunction{
    public TestReduceJniFunc() {
    }

    public TestReduceJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doReduceTest(VectorBean v1, VectorBean v2);
}

public class JniFunctionPrototypeTest {
    JniFunctionPrototype prototype;
    TestUtils testUtils;
    JniDirAccessor jniDirAccessor;
    @Before
    public void setUp() throws Exception {
        testUtils = new TestUtils(JniFunctionPrototype.class);
        testUtils.initTestDir();
        SparkJni.reset();
        SparkJni.setNativePath(testUtils.defaultTestFolder);
        SparkJni.registerContainer(VectorBean.class);
        SparkJni.registerJniFunction(TestMapperJniFunc.class);
        SparkJni.registerJniFunction(TestReduceJniFunc.class);
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
        assertNotNull(mapperHeader);
        List<JniFunctionPrototype> jniFunctionPrototypes = mapperHeader.getJniFunctions();
        assertEquals(jniFunctionPrototypes.size(), 1);

        JniFunctionPrototype functionPrototype = jniFunctionPrototypes.get(0);
        assertEquals(functionPrototype.getCppReturnType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionPrototype.getParameterList().size(), 1);
        assertEquals(functionPrototype.getParameterList().get(0).getCppClassName(), "CPPVectorBean");
    }

    @Test
    public void testReduceJniFunctionPrototype() throws Exception {
        JniHeaderHandler reduceHeader = getJniHeaderByName(jniDirAccessor.getJniHeaderHandlers(), "TestReduceJniFunc");
        assertNotNull(reduceHeader);
        List<JniFunctionPrototype> jniFunctionPrototypes = reduceHeader.getJniFunctions();
        assertEquals(jniFunctionPrototypes.size(), 1);

        JniFunctionPrototype functionPrototype = jniFunctionPrototypes.get(0);
        assertEquals(functionPrototype.getCppReturnType().getCppClassName(), "CPPVectorBean");
        assertEquals(functionPrototype.getParameterList().size(), 2);
        assertEquals(functionPrototype.getParameterList().get(0).getCppClassName(), "CPPVectorBean");
        assertEquals(functionPrototype.getParameterList().get(1).getCppClassName(), "CPPVectorBean");
    }
}