package org.heterojni.sparkjni.jniLink.linkHandlers;

import junit.framework.TestCase;
import org.heterojni.sparkjni.dataLink.CppBean;
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapperTest;
import org.heterojni.sparkjni.utils.JniLinkHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by tudor on 10/12/16.
 */
@RunWith (PowerMockRunner.class)
@PrepareForTest (JniLinkHandler.class)
public class NativeFunctionWrapperTest {
    private static String METHOD_IMPLEMENTATION =
            "JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){\n" +
                    "\tjclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);\n" +
                    "\tCPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);\n" +
                    "\tCPPVectorBean returnedCppvectorbean0;\n" +
                    "\treturn returnedCppvectorbean0.getJavaObject();\n" +
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