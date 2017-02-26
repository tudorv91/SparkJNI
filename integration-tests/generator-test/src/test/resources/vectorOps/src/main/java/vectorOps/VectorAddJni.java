package vectorOps;

import sparkjni.jniLink.jniFunctions.JniReduceFunction;
import sparkjni.utils.annotations.JniFunction;

/**
 * Classes annotated with @JniFunction are automatically registered with SparkJNI as JNI functions.
 * These classes need to extend either JniMapFunction or JniReduceFunction and override the default and parametric constructor, just like in this example.
 * Last, a native prototype needs to be declared so it can be populated with native functionality by SparkJNI (in this case: reduceVectorAdd)
 */
@JniFunction
public class VectorAddJni extends JniReduceFunction {
    @SuppressWarnings("unused")
    public VectorAddJni() {
    }

    public VectorAddJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    @SuppressWarnings("unused")
    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}