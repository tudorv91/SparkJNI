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

    public native PairHmmBean calculateSoftware(PairHmmBean bean);
    public native PairHmmBean calculateHardware(PairHmmBean bean);
}
