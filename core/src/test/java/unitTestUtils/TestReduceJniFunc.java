package unitTestUtils;

import sparkjni.jniLink.jniFunctions.JniReduceFunction;
import sparkjni.utils.annotations.JniFunction;

@JniFunction
public class TestReduceJniFunc extends JniReduceFunction {
    public TestReduceJniFunc() {
    }

    public TestReduceJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doReduceTest(VectorBean v1, VectorBean v2);
}
