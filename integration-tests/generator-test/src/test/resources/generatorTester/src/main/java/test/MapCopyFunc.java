package test;

import sparkjni.jniLink.jniFunctions.JniMapFunction;
import sparkjni.utils.annotations.JniFunction;
import test.DoubleArray;

/**
 * Created by tudor on 11/20/16.
 */
@JniFunction
public class MapCopyFunc extends JniMapFunction {
    public MapCopyFunc() {
        super();
    }

    public MapCopyFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native DoubleArray arrayCopy(DoubleArray doubleArray);
}
