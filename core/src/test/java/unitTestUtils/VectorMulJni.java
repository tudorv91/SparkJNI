package unitTestUtils;

import sparkjni.jniLink.jniFunctions.JniMapFunction;

/**
 * Created by Tudor on 9/8/16.
 */
public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}
