package unitTestUtils;

import sparkjni.jniLink.jniFunctions.JniMapFunction;
import sparkjni.utils.annotations.JniFunction;

/**
 * Created by Tudor on 9/24/16.
 */

@JniFunction
public class TestMapperJniFunc extends JniMapFunction {
    public TestMapperJniFunc() {
    }

    public TestMapperJniFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean doMapTest(VectorBean vectorBean);
}
