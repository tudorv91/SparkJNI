package org.tudelft.ewi.ceng.methods;

import org.tudelft.ewi.ceng.CppClass;

/**
 * Created by Tudor on 8/6/16.
 */
public abstract class NativeMethod {
    CppClass ownerClass;
    String returnType;
    String methodBody;
    String methodName;
    String ownerClassName;
    String accessor;
    boolean isValid = true;

    public NativeMethod(CppClass cppClass){
        ownerClass = cppClass;
        ownerClassName = cppClass.getCppClassName();
    }

    protected NativeMethod() {
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public String getReturnType(){
        return returnType;
    }
    public String getMethodBody(){
        return methodBody;
    }
    public String getAccessor(){
        return accessor;
    }
    public String getMethodName() {
        return methodName;
    }
}
