package org.tudelft.ewi.ceng.examples.vectorOps;

import org.tudelft.ewi.ceng.sparkjni.jniFunctions.JniMapFunction;

/**
 * Created by root on 9/8/16.
 */
public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}
