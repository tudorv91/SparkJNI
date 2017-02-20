package sparkjni.jniLink.linkContainers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import sparkjni.dataLink.CppBean;
import sparkjni.jniLink.linkHandlers.ImmutableFunctionSignatureMapperProvider;
import sparkjni.utils.JniLinkHandler;
import unitTestUtils.VectorBean;
import unitTestUtils.VectorMulJni;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FunctionSignatureMapperTest {
    private static String FULLY_QUALIFIED_JAVA_CLASS_NAME = "unitTestUtils.VectorMulJni";
    private static String PARAMETERS_LINE = "  (JNIEnv *, jobject, jobject);";
    private static String[] TOKENS = {"JNIEXPORT", "jobject", "JNICALL", "Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul"};

    private TypeMapper vectorBeanTypeMapper;
    private EntityNameMapper mapVectorMulMethodNameMapper;
    public FunctionSignatureMapper functionSignatureMapper = null;

    @Mock
    private JniLinkHandler jniLinkHandlerMock;
    @Mock
    private CppBean vectorCppBeanMock;

    @Before
    public void setUp(){
        initMocks(this);
        if(eitherMockIsNull()) {
            initMocks(FunctionSignatureMapperTest.class);
        }

        when(vectorCppBeanMock.getCppClassName()).thenReturn("CPPVectorBean");
        when(jniLinkHandlerMock.getJavaClassByName(FULLY_QUALIFIED_JAVA_CLASS_NAME)).thenReturn(VectorMulJni.class);
        when(jniLinkHandlerMock.getContainerByJavaClass(VectorBean.class)).thenReturn(vectorCppBeanMock);

        vectorBeanTypeMapper = ImmutableTypeMapper.builder()
                .jniType("jobject").cppType(vectorCppBeanMock).javaType(VectorBean.class)
                .build();
        mapVectorMulMethodNameMapper = ImmutableEntityNameMapper.builder()
                .cppName("mapVectorMul")
                .jniName("Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul")
                .javaName("mapVectorMul")
                .build();
        initFunctionSignatureMapper();
        assertNotNull(functionSignatureMapper);
    }

    @Test
    public void staticMethodTest(){
        assertFalse(functionSignatureMapper.staticMethod());
    }

    @Test
    public void parameterListTest(){
        assertEquals(1, functionSignatureMapper.parameterList().size());
//        assertEquals(vectorBeanTypeMapper, functionSignatureMapper.parameterList().get(0));
    }

    @Test
    public void functionNameMapperTest(){
        assertEquals(mapVectorMulMethodNameMapper, functionSignatureMapper.functionNameMapper());
    }

    private void initFunctionSignatureMapper(){
        try {
            functionSignatureMapper = ImmutableFunctionSignatureMapperProvider.builder()
                    .fullyQualifiedJavaClass(FULLY_QUALIFIED_JAVA_CLASS_NAME)
                    .parametersLine(PARAMETERS_LINE)
                    .tokens(TOKENS)
                    .build().buildFunctionSignatureMapper();
        } catch (Exception ex){
            ex.printStackTrace();
            fail();
        }
    }

    private boolean eitherMockIsNull() {
        if(jniLinkHandlerMock == null)
            return false;
        if(vectorCppBeanMock == null)
            return false;
        return true;
    }
}
