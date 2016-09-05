# SparkJNI

# Build Java instructions
If you have Maven installed, go to the root of directory and run ```sudo mvn clean install```. This creates the .jar file in the target folder. If not, just skip this step, this public repo contains the pre-built jar file.

# Environment variables
In order to run the example PairHMM file, you need to set up the following environment variables:
```
export SPARK_HOME=<where-spark-is-installed>
export JAVA_HOME=<where-java-is-installed>
export SPARK_JNI=<address-of-the-sparkjni-clone>
```

# Run
Move to the C/C++ source folder:
```
cd cppSrc/
```
And run. The command below submits the application to Spark. The size of the input batch should be a power of 2, up to 32768 (or bigger, but increase the Spark memory settings, if the system supports it). The path to the C/C++ sources is (in this case) in the ```cppSrc``` folder.
```
./run.sh <path-to-cpp-sources> pairhmm <size-of-batch-input-size>
```

#Inspect results
The runtime benchmarks can be inspected in ```resultsJava.csv```. All values are in seconds.

