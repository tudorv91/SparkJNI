package generator.packageTwo;

import sparkjni.jniLink.jniFunctions.JniMapFunction;
import sparkjni.utils.jniAnnotations.JNI_functionClass;

@JNI_functionClass
public class SomeMapFunction extends JniMapFunction{
    public SomeMapFunction() {
        super();
    }

    public SomeMapFunction(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }
}
