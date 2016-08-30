package org.tudelft.ewi.ceng.methods;

import org.tudelft.ewi.ceng.CppClass;
import org.tudelft.ewi.ceng.JniUtils;

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