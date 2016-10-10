package org.heterojni.examples.vectorOps;

import org.heterojni.sparkjni.jniLink.jniFunctions.JniReduceFunction;

/**
 * Created by root on 9/9/16.
 */
public class VectorAddJni extends JniReduceFunction {
    public VectorAddJni() {
    }

    public VectorAddJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}
