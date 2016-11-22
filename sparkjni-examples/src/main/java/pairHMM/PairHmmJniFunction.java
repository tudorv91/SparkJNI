package pairHMM;
import sparkjni.jniLink.jniFunctions.JniMapFunction;

/**
 * Created by Tudor on 8/13/16.
 */
public class PairHmmJniFunction extends JniMapFunction {
    public PairHmmJniFunction(){}
    public PairHmmJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native PairHmmJniFunction calculateSoftware(PairHmmBean bean);
    public native PairHmmJniFunction calculateHardware(PairHmmBean bean);
}
