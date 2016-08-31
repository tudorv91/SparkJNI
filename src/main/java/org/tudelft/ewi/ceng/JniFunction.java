package org.tudelft.ewi.ceng;

import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.function.Function;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by root on 7/20/16.
 * Abstract class to be inherited for all JNI function user implementation classes.
 */
public abstract class JniFunction<T1, R> implements Function<T1, R>{
    private String nativeLibPath;
    private String nativeFunctionName;
    private Method nativeMethod = null;

    public JniFunction(){}

    public JniFunction(String nativeLibPath, String nativeFunctionName){
        this.nativeLibPath = nativeLibPath;
        this.nativeFunctionName = nativeFunctionName;
    }

    /**
     * @note It is critical to not use generic types defining native methods in inherited user classes.
     * Since we want to avoid runtime C/C++ compilation, we cannot get type information statically (before the function is actually called,
     * instantiated..
     * @TODO Try find type from .java file context.
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public R call(T1 o) {
        long startProc = System.nanoTime();
        R retObj = null;
        Class thisClass = this.getClass();
        Method[] methods = thisClass.getMethods();
        if(nativeLibPath.contains("/"))
            System.load(nativeLibPath);
        else
            try{
                System.load(SparkFiles.get(nativeLibPath));
            } catch(Exception e){
                System.load(nativeLibPath);
            }

        for(Method method: methods){
            if(Modifier.isNative(method.getModifiers()) && method.getName().equals(nativeFunctionName)){
                nativeMethod = method;
            }
        }

        System.out.println(String.format("Calling method %s", nativeFunctionName));

        if(nativeMethod == null)
        {
            String errStr = "Could not find method "+nativeFunctionName+" in class "+this.getClass().getName();
            System.err.println(errStr);
            throw new RuntimeException(errStr);
        }
        try {
            retObj = (R) nativeMethod.invoke(this, o);
        } catch(Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage());
        }

        ((Bean)retObj).setStartRun(startProc);
        ((Bean)retObj).setEndRun(System.nanoTime());
        return retObj;
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
}