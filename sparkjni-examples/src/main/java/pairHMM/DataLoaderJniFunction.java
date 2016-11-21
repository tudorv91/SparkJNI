package pairHMM;
import sparkjni.jniLink.jniFunctions.JniMapFunction;

/**
 * Created by Tudor on 8/17/16.
 */
public class DataLoaderJniFunction extends JniMapFunction {
    public DataLoaderJniFunction(){}
    public DataLoaderJniFunction(String nativeLibName, String nativeFunctionName){
        super(nativeLibName, nativeFunctionName);
    }

    public native ByteArrBean callDataLoader(WorkloadPairHmmBean bean);
}
