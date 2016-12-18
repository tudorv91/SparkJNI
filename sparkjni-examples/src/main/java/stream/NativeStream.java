package stream;

import sparkjni.jniLink.jniFunctions.JniMapFunction;

public class NativeStream extends JniMapFunction{
    public NativeStream() {
    }

    public NativeStream(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native StreamVectors stream(StreamVectors inputVectors);
}
