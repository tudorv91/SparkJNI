package sparkjni.jniLink.linkHandlers;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import sparkjni.dataLink.CppBean;
import sparkjni.jniLink.linkContainers.FunctionSignatureMapperTest;
import sparkjni.utils.JniLinkHandler;

/**
 * Created by tudor on 10/12/16.
 */
@RunWith (PowerMockRunner.class)
@PrepareForTest (JniLinkHandler.class)
public class NativeFunctionWrapperTest {
    private static String METHOD_IMPLEMENTATION ="JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){\n" +
            "\tjclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);\n" +
            "\tstd::shared_ptr<CPPVectorBean> cppvectorbean0 = std::make_shared<CPPVectorBean>(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);\n" +
            "\tstd::shared_ptr<CPPVectorBean> CPPVectorBean_ret = mapVectorMul(cppvectorbean0, cppvectorbean_jClass, jniEnv);\n" +
            "\tjobject CPPVectorBean_retJavaObj = CPPVectorBean_ret->getJavaObject();\n" +
            "\treturn CPPVectorBean_retJavaObj;\n" +
            "}";
    private NativeFunctionWrapper nativeFunctionWrapper;
    private static FunctionSignatureMapperTest functionSignatureMapperTest;

    @Mock
    public JniLinkHandler jniLinkHandlerMock;
    @Mock
    public CppBean vectorCppBeanMock;

    @Test
    public void nativeFunctionWrapper() {
        MockitoAnnotations.initMocks(NativeFunctionWrapperTest.class);
        functionSignatureMapperTest = new FunctionSignatureMapperTest(jniLinkHandlerMock, vectorCppBeanMock);
        functionSignatureMapperTest.setUp();
        nativeFunctionWrapper = ImmutableNativeFunctionWrapper.builder()
                .functionSignatureMapper(functionSignatureMapperTest.functionSignatureMapper)
                .build();
        TestCase.assertNotNull(nativeFunctionWrapper);
        Assert.assertEquals(METHOD_IMPLEMENTATION, nativeFunctionWrapper.generateNativeMethodImplementation());
    }
}