package vectorOps;

import sparkjni.jniLink.jniFunctions.JniMapFunction;
import sparkjni.utils.annotations.JniFunction;

/**
 * Classes annotated with @JniFunction are automatically registered with SparkJNI as JNI functions.
 * These classes need to extend either JniMapFunction or JniReduceFunction and override the default and parametric constructor, just like in this example.
 * Last, a native prototype needs to be declared so it can be populated with native functionality by SparkJNI (in this case: mapVectorMul)
 */
@JniFunction
public class VectorMulJni extends JniMapFunction {
    @SuppressWarnings("unused")
    public VectorMulJni() {
    }

    public VectorMulJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    @SuppressWarnings("unused")
    public native VectorBean mapVectorMul(VectorBean inputVector);
}