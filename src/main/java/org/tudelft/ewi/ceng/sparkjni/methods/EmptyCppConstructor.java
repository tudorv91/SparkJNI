package org.tudelft.ewi.ceng.sparkjni.methods;

import org.tudelft.ewi.ceng.sparkjni.utils.CppClass;
import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

/**
 * Created by root on 8/16/16.
 */
public class EmptyCppConstructor extends NativeMethod{
    public EmptyCppConstructor(CppClass cppClass) {
        super(cppClass);
    }

    public EmptyCppConstructor() {
    }

    public String generateNonArgConstructorPrototype(){
        return String.format(JniUtils.CONSTRUCTOR_WITH_NATIVE_ARGS_PROTOTYPE_STR,
                ownerClass.getCppClassName(), "");
    }

    public String generateNonArgConstructorImpl(){
        return String.format(JniUtils.CONSTRUCTOR_WITH_NATIVE_ARGS_IMPL_STR,
                ownerClassName, ownerClassName, "", "");
    }
}