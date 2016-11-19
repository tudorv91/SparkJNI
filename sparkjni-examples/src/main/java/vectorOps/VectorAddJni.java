package vectorOps;

import sparkjni.jniLink.jniFunctions.JniReduceFunction;

public class VectorAddJni extends JniReduceFunction {
    public VectorAddJni() {
    }

    public VectorAddJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}