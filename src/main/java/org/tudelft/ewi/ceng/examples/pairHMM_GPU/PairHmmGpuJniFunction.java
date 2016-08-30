package org.tudelft.ewi.ceng.examples.pairHMM_GPU;

import org.tudelft.ewi.ceng.JniFunction;

/**
 * Created by root on 8/26/16.
 */
public class PairHmmGpuJniFunction extends JniFunction{
    public PairHmmGpuJniFunction() {
    }

    public PairHmmGpuJniFunction(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native Object runContained(Object input);
}
