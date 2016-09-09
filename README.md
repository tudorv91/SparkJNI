# SparkJNI
This framework is meant to reduce the development effort of native-accelerated Spark applications by means of targeted JNI wrappers for the control and the data transfer links.

# Build Java instructions
If you have Maven installed, go to the root of directory and run ```sudo mvn clean install```. This creates the .jar file in the target folder. If not, just skip this step, this public repo contains the pre-built jar file.

# Environment variables
In order to run the example PairHMM file, you need to set up the following environment variables:
```
export SPARK_HOME=<where-spark-is-installed>
export JAVA_HOME=<where-java-is-installed>
export SPARK_JNI=<address-of-the-sparkjni-clone>
```

# Examples
# Vector Operations
We define a list of vectors on which we apply a ```map``` transformation, than a reduce. The main program is given in the VectorOpsMain.java source file, in ..examples.VectorOps package. We use one container (VectorBean):
```
@JNI_class public class VectorBean extends Bean {
    @JNI_field int[] data;

    @JNI_method public VectorBean(@JNI_param(target = "data") int[] data) {
        this.data = data;
    }
}
```
Next, we define the two classes used for the reduce and the map transformations:
```
public class VectorAddJni extends JniReduceFunction {
    public VectorAddJni() {
    }

    public VectorAddJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}
```
and
```
public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativeLibPath, String nativeFunctionName) {
        super(nativeLibPath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}
```
These are used in the main class (VectorOpsMain):
```
public class VectorOpsMain {
...
    private static void initSparkJNI(String[] args){
        if(args.length >= 3){
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath>");
        }

        JniFrameworkLoader.setJdkPath(jdkPath);
        JniFrameworkLoader.setNativePath(nativePath);
        JniFrameworkLoader.setDoGenerateMakefile(true);
        JniFrameworkLoader.setDoBuild(true);

        // Register control and data transfer links (classes).
        JniFrameworkLoader.registerContainer(VectorBean.class);
        JniFrameworkLoader.registerJniFunction(VectorMulJni.class);
        JniFrameworkLoader.registerJniFunction(VectorAddJni.class);
        JniFrameworkLoader.deploy(appName, appName + ".cpp", null);
    }

    public static void main(String[] args){
        initSparkJNI(args);
        String libPath = String.format("%s/%s.so", nativePath, appName);
        JavaRDD<VectorBean> vectorsRdd = getSparkContext().parallelize(generateVectors(2, 4));
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
        debugRes(results);
    }
...
}
```
Last, we have to populate the native functions with desired behavior, in the ```vectorOps.cpp``` kernel file:
```
...
JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *env, jobject caller, jobject v1obj, jobject v2obj){
    jclass vectorClass = env->GetObjectClass(v1obj);
    CPPVectorBean v1(vectorClass, v1obj, env);
    CPPVectorBean v2(vectorClass, v2obj, env);
    int vectorLength = v1.getdata_length();
    for(int idx = 0; idx < vectorLength; idx++)
        v1.getdata()[idx] += v2.getdata()[idx];
    return v1.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *env, jobject caller, jobject vectObj){
    jclass vectorClass = env->GetObjectClass(vectObj);
    CPPVectorBean v1(vectorClass, vectObj, env);
    int vectorLength = v1.getdata_length();
    for(int idx = 0; idx < vectorLength; idx++)
        v1.getdata()[idx] *= 2;
    return v1.getJavaObject();
}
```
The program can be run then, by packaging the jar and with ```./spark-submit```.
# PairHMM
First, flash the AlphaData card with the .dat file provided in the PairHMM sources folder.
Then, move to the C/C++ source folder:
```
cd cppSrc/
```
And run. The command below submits the application to Spark. The size of the input batch should be a power of 2, up to 32768 (or bigger, but increase the Spark memory settings, if the system supports it). The path to the C/C++ sources is (in this case) in the ```cppSrc``` folder.
```
./run.sh <path-to-cpp-sources> pairhmm <size-of-batch-input-size>
```

#Inspect results
The runtime benchmarks can be inspected in ```resultsJava.csv```. All values are in seconds.

