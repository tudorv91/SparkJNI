package org.heterojni.examples.sparkJNIPi;

import org.heterojni.sparkjni.jniLink.jniFunctions.JniMapFunction;

public class RandOpenclPiMap extends JniMapFunction {
    public RandOpenclPiMap() {
    }

    public RandOpenclPiMap(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native SumArray randToSum(RandNumArray input);
}
