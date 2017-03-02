# SparkJNI
This framework is a powerful platform which enables integration of native kernels (FPGA, OpenCL, C++) with Apache Spark, targeting faster execution of Big Data applications. 
SparkJNI is  meant to reduce the development effort of native-accelerated Spark applications by means of
offloading developers of handling the JNI programming.

# Setup
In order to run the simplest example, first set a system-wide environment variable JAVA_HOME with the location of your JDK installation (minimum JDK 7) (create a .sh file with ```export JAVA_HOME=<where-java-is-installed>``` in ```/etc/profile.d/``` and make sure it is loaded by logging in and out).

If you don't have Maven install, please install it with ```sudo apt-get install maven```.

Go to the root of the repository and run ```sudo mvn clean install```. 
By this, you will do a full build and test of the project and its modules. 
A build without running unit and integration tests can be done by running ```sudo mvn clean install -DskipTests```. A full build should pass all tests.
If build is run in a resource-constrained environment, then an extra parameter should be provided to the Maven command:
```mvn clean install -DtestMode=constrained``` to ensure tests are not failing due to memory requirements.

### Annotating a sample SparkJNI-enabled application
SparkJNI generates JNI native code based on annotations found on user-defined types and functions. 
These annotations are demoed next based on a example application: we define a list of vectors on which we apply 
a ```map``` transformation, than a reduce. The main program is given in the ```VectorOpsMain.java``` source file, 
in the sparkjni-examples module in the vectorOps package. 

#### SparkJNI containers
SparkJNI containers are user-defined types that can be passed in and retrieved from SparkJNI-enabled transformations.
These containers hold data of interest that can be processed in the native code.

Going back to the sample VectorOps application, the container which holds the vector contents, ```VectorBean``` is defined below:
```
@JNI_class public class VectorBean extends JavaBean {
    @JNI_field int[] data;

    @JNI_method public VectorBean(@JNI_param(target = "data") int[] data) {
        this.data = data;
    }
}
```
This container, like any other user-defined containers, needs to be annotated with ```@JNI_class``` and extends the SparkJNI-defined ```JavaBean```.
Next, each container field that we want exposed in the native side should be annotated with ```@JNI_field```. 
Last, a setter-constructor (that initializes only the ```@JNI_field``` annotated fields) should be implemented and annotated with ```@JNI_method```.

#### JNI functions
JNI functions are user-defined classes which are used to transfer control to native code.
These classes need to satisfy the following requirements:

1. Extend one of the ```JniReduceFunction``` or ```JniMapFunction``` SparkJNI types.

2. Declare a public default constructor. 

3. Override the two-argument constructor from their supertype.

4. Contain at least a declaration of a native Java function (with the modifier ```native```), with the required number of arguments.

In the case of the example application, we define the two classes used for the reduce and the map transformations:
```
@JniFunction
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
@JniFunction.
public class VectorMulJni extends JniMapFunction {
    public VectorMulJni() {
    }

    public VectorMulJni(String nativePath, String nativeFunctionName) {
        super(nativePath, nativeFunctionName);
    }

    public native VectorBean mapVectorMul(VectorBean inputVector);
}
```

# Deployment modes
There are two ways of building Spark applications with SparkJNI:

1. By using the SparkJNI Maven Generator plugin. (recommended)

2. By programmatically configuring SparkJNI before the actual application execution flow. 

## SparkJNI generator plugin
The Generator plugin works by retrieving user-defined classes annotated with SparkJNI annotations. The native code is automatically generated at build time (e.g. maven install). 
In order to use SparkJNI in this manner, include the following build configuration in the application's pom:
```
<project>
...
    <build>
        <plugins>
            <plugin>
                <groupId>tudelft.ceng.tvoicu</groupId>
                <artifactId>sparkjni-generator-plugin</artifactId>
                <version>0.2</version>
                <executions>
                    <execution>
                        <phase>install</phase>
                        <goals>
                            <goal>sparkjni-generator</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
...
</project>
```
The build will succeed only if SparkJNI has been previously built locally (i.e. the generator artifact exists in the local Maven repository).

## Deploying SparkJNI programmatically
For more experienced users (or in cases where Maven is not used), SparkJNI can be configured before application deployment. 
The following example showcases constructing a sample application, including the configuration of SparkJNI.

These are used in the main class (VectorOpsMain). The following code snippet showcases the programmatic configuration.
A SparkJNI instance can be created using the built-in builder and by setting two mandatory parameters:

1. The **native path** points to the directory where the native code will be generated, including the template kernel file.

2. The **app name** indicates the kernel file and native library name (*native/path/appName.cpp* and *native/path/appName.so*).
```
private static void initSparkJNI(){
    SparkJni sparkJniProvider = new SparkJniSingletonBuilder()
            .nativePath(nativePath)
            .appName(appName)
            .build();
    sparkJniProvider.setDeployMode(new DeployMode(JUST_BUILD))
            .addToClasspath(sparkjniClasspath, examplesClasspath);
    // Register control and data transfer links (classes).
    sparkJniProvider.registerContainer(VectorBean.class)
            .registerJniFunction(VectorMulJni.class)
            .registerJniFunction(VectorAddJni.class);
    sparkJniProvider.deploy();
}
```
The native code deploy mode is by default set to *FULL_GENERATE_AND_BUILD*. However, in advanced use-cases,
it can be set to different deploy modes (e.g. in this case it is *JUST_BUILD*, so it does not overwrite native source files).

SparkJNI works based on type mappings and it needs *Containers* and *JniFunctions* that will be used in the target application to be indexed.
In the above example, they are registered with the previously-built SparkJni instance.

Last, but most important, code generation needs to be triggered after SparkJNI configuration with *deploy()*.
```
public static void main(String[] args){
    initSparkJNI();
    JavaRDD<VectorBean> vectorsRdd = getSparkContext().parallelize(generateVectors(2, 4));
    JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
    VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
}
```
After successful deployment of a SparkJNI configuration, a Spark application can use the populated native functionality. 
Instead of using anonymous classes in transformations, you can use previously-defined JniFunctions). 
These functions need to be instantiated with the native library path and target native function as arguments).

### Native code
After the Java application has been constructed, annotated with SparkJNI annotations and built (depending on the SparkJNI 
deployment mode chosen), native code functionality can be inserted in the generated native functions. 

In our VectorOps example, we have to populate the native functions with desired behavior, in the ```vectorOps.cpp``` kernel file:
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
Now, if the application has been deployed using the Generator plugin, the project needs to be built again (```mvn clean install```). 
Otherwise, if SparkJNI has been programmatically configured, the application can be run (if it had enabled native code build).
The program can be run then, either from the IDE or by packaging the jar and with ```./spark-submit```.

## Virtualized deployment with Vagrant
For means of automatic deployment and *vanilla* testing of the SparkJNI framework, one can use the vagrant build
script ```deployVagrantTestingVM.sh```. This launches an Ubuntu 16.04 virtual machine, installs all dependencies
and builds and tests the latest SparkJNI GitHub version.
