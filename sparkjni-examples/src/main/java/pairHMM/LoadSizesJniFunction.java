package pairHMM;
import sparkjni.jniLink.jniFunctions.JniMapFunction;

/**
 * Created by Tudor on 8/16/16.
 */
public class LoadSizesJniFunction extends JniMapFunction {
    public LoadSizesJniFunction(){}
    public LoadSizesJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native WorkloadPairHmmBean loadSizes(SizesBean bean);
}