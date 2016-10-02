package org.tudelft.ceng.examples.pairHMM;
import org.tudelft.ceng.sparkjni.jniFunctions.JniMapFunction;

/**
 * Created by root on 8/16/16.
 */
public class LoadSizesJniFunction extends JniMapFunction {
    public LoadSizesJniFunction(){}
    public LoadSizesJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native WorkloadPairHmmBean loadSizes(SizesBean bean);
}