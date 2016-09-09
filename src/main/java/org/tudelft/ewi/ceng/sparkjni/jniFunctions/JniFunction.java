package org.tudelft.ewi.ceng.sparkjni.jniFunctions;

import org.apache.spark.SparkFiles;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by root on 7/20/16.
 * Abstract class to be inherited for all JNI function user implementation classes.
 */
public abstract class JniFunction<T1, R> implements Serializable{
    String nativeLibPath;
    String nativeFunctionName;

    public JniFunction(){}

    public JniFunction(String nativeLibPath, String nativeFunctionName){
        this.nativeLibPath = nativeLibPath;
        this.nativeFunctionName = nativeFunctionName;
    }

    public ArrayList<Class> getParamTypes(String methodName){
        ArrayList<Class> paramTypes = new ArrayList<>();
        Class callerClass = this.getClass();
        for(Method method: callerClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)){
                for(Class paramType: method.getParameterTypes())
                    paramTypes.add(paramType);
            }
            return paramTypes;
        }

        return null;
    }

    void loadNativeLib(){
        if(nativeLibPath.contains("/"))
            System.load(nativeLibPath);
        else
            try{
                System.load(SparkFiles.get(nativeLibPath));
            } catch(Exception e){
                System.load(nativeLibPath);
            }
    }
}