package sparkjni.jniLink;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapper;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.jniLink.linkHandlers.FunctionSignatureMapperProvider;
import sparkjni.jniLink.linkHandlers.ImmutableJniRootContainerProvider;
import sparkjni.utils.JniUtils;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;
import unitTestUtils.TestMapperJniFunc;
import unitTestUtils.TestReduceJniFunc;
import unitTestUtils.VectorBean;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
        sparkJni.getJniHandler().deployLink(true);
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