package unitTestUtils;

import sparkjni.jniLink.jniFunctions.JniReduceFunction;

/**
 * Created by Tudor on 9/9/16.
 */
public class VectorAddJni extends JniReduceFunction {
    public VectorAddJni() {
    }

    public VectorAddJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}
