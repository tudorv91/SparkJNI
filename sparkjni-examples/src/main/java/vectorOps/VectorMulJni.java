package vectorOps;

import sparkjni.jniLink.jniFunctions.JniMapFunction;

public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}