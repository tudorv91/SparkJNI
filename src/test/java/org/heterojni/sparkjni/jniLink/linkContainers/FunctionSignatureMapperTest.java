package org.heterojni.sparkjni.jniLink.linkContainers;

import org.heterojni.examples.vectorOps.VectorBean;
import org.heterojni.examples.vectorOps.VectorMulJni;
import org.heterojni.sparkjni.dataLink.CppBean;
import org.heterojni.sparkjni.jniLink.linkHandlers.ImmutableFunctionSignatureMapperProvider;
import org.heterojni.sparkjni.utils.JniLinkHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JniLinkHandler.class)
public class FunctionSignatureMapperTest {
    private static String FULLY_QUALIFIED_JAVA_CLASS_NAME = "org.heterojni.examples.vectorOps.VectorMulJni";
    private static String PARAMETERS_LINE = "  (JNIEnv *, jobject, jobject);";
    private static String[] TOKENS = {"JNIEXPORT", "jobject", "JNICALL", "Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul"};

    private TypeMapper vectorBeanTypeMapper;
    private EntityNameMapper mapVectorMulMethodNameMapper;
    public FunctionSignatureMapper functionSignatureMapper = null;

    @Mock
    public JniLinkHandler jniLinkHandlerMock;
    @Mock
    public CppBean vectorCppBeanMock;

    public FunctionSignatureMapperTest(){}

    public FunctionSignatureMapperTest(JniLinkHandler jniLinkHandlerMock, CppBean vectorCppBeanMock) {
        this.jniLinkHandlerMock = jniLinkHandlerMock;
        this.vectorCppBeanMock = vectorCppBeanMock;
    }

    @Before
    public void setUp(){
        if(eitherMockIsNull()) {
            initMocks(FunctionSignatureMapperTest.class);
        }
        PowerMockito.mockStatic(JniLinkHandler.class);

        when(JniLinkHandler.getJniLinkHandlerSingleton()).thenReturn(jniLinkHandlerMock);
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
        assertEquals(vectorBeanTypeMapper, functionSignatureMapper.parameterList().get(0));
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