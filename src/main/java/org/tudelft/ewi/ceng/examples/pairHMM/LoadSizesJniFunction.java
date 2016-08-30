package org.tudelft.ewi.ceng.examples.pairHMM;

import org.tudelft.ewi.ceng.JniFunction;

/**
 * Created by root on 8/16/16.
 */
public class LoadSizesJniFunction extends JniFunction {
    public LoadSizesJniFunction(){}
    public LoadSizesJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native WorkloadPairHmmBean loadSizes(SizesBean bean);
}