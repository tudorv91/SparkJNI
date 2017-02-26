package memoryCopy;

import sparkjni.jniLink.jniFunctions.JniMapFunction;

/**
 * Created by tudor on 11/20/16.
 */
public class MapCopyFunc extends JniMapFunction {
    public MapCopyFunc() {
        super();
    }

    public MapCopyFunc(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native DoubleArray arrayCopy(DoubleArray doubleArray);
}
