package pairHMM;
import sparkjni.jniLink.jniFunctions.JniMapFunction;

public class PairHmmJniFunction<R, T> extends JniMapFunction<R, T> {
    public PairHmmJniFunction(){}
    public PairHmmJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native PairHmmBean calculateSoftware(PairHmmBean bean);
    public native PairHmmBean calculateHardware(PairHmmBean bean);
}