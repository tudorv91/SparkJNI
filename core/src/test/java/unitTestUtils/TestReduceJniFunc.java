package unitTestUtils;

import sparkjni.jniLink.jniFunctions.JniReduceFunction;
import sparkjni.utils.jniAnnotations.JNI_functionClass;

/**
 * Created by tudor on 11/19/16.
 */
@JNI_functionClass
public class TestReduceJniFunc extends JniReduceFunction {
    public TestReduceJniFunc() {
    }

    public TestReduceJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doReduceTest(VectorBean v1, VectorBean v2);
}
