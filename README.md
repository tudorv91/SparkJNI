# SparkJNI
This framework is a powerful platform which enables integration of native kernels (FPGA, OpenCL, C++) with Apache Spark, targeting faster execution of Big Data applications. Second, SparkJNI is  meant to reduce the development effort of native-accelerated Spark applications by means of targeted JNI wrappers for the control and the data transfer links.

# Build Java instructions
In order to run the simplest example, first set a system-wide environment variable JAVA_HOME with the location of your JDK installation (minimum JDK 7) (create a .sh file with ```export JAVA_HOME=<where-java-is-installed>``` in ```/etc/profile.d/``` and make sure it is loaded by logging in and out).

If you don't have Maven install, please install it with ```sudo apt-get install maven```.

Go to the root of the repository and run ```sudo mvn clean install```. By this, you will do a full build and test of the project and its modules. A build without running unit and itegration tests can be done by running ```sudo mvn clean install -DskipTests```. A full build should pass all tests.

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

    public VectorAddJni(String nativePath, String nativeFunctionName) {
        super(nativePath, nativeFunctionName);
    }

    public native VectorBean reduceVectorAdd(VectorBean v1, VectorBean v2);
}
```
and
```
public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativePath, String nativeFunctionName) {
        super(nativePath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}
```
These are used in the main class (VectorOpsMain):
```
public class VectorOpsMain {
...
    private static void initSparkJNI(){
...
        SparkJni sparkJni = new SparkJniSingletonBuilder()
                .nativePath(nativePath)
                .appName(appName)
                .build();
        sparkJni.setDeployMode(new DeployMode(JUST_BUILD))
                .addToClasspath(sparkjniClasspath, examplesClasspath);
        // Register control and data transfer links (classes).
        sparkJni.registerContainer(VectorBean.class)
                .registerJniFunction(VectorMulJni.class)
                .registerJniFunction(VectorAddJni.class);
        sparkJni.deploy();
    }

    public static void main(String[] args){
        initSparkJNI();
...
        JavaRDD<VectorBean> vectorsRdd = getSparkContext().parallelize(generateVectors(2, 4));
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
...
    }
...
}
```
Last, we have to populate the native functions with desired behavior, in the ```vectorOps.cpp``` kernel file:
```
...
std::shared_ptr<CPPVectorBean> reduceVectorAdd(std::shared_ptr<CPPVectorBean>& cppvectorbean0, std::shared_ptr<CPPVectorBean>& cppvectorbean1,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	for(int idx = 0; idx < cppvectorbean0->getdata_length(); idx++)
		cppvectorbean0->getdata()[idx] += cppvectorbean1->getdata()[idx];
	return cppvectorbean0;
}

std::shared_ptr<CPPVectorBean> mapVectorMul(std::shared_ptr<CPPVectorBean>& cppvectorbean0,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	for(int idx = 0; idx < cppvectorbean0->getdata_length(); idx++)
		cppvectorbean0->getdata()[idx] *= 2;
	return cppvectorbean0;
}
```
The program can be run then, either from the IDE or by packaging the jar and with ```./spark-submit```.
