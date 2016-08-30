# SparkJNI

# Build Java instructions
First, make sure you have Maven installed. Next, go to the root of directory and run sudo mvn clean install. This creates the .jar file in the target folder.

#Build native instructions
Create a folder on your user space and input the pairhmm.cpp kernel file (for running the example PairHMM software application on Spark).

# Run
Go to the Spark home directory. Then,
```
./bin/spark-submit --driver-java-options "-XX:+ShowMessageBoxOnError" --class org.tudelft.ewi.ceng.examples.pairHMM.PairHmmMain ${PATH_TO_SPARKJNI_JAR} ${NATIVE_CODE_PATH} ${APP_NAME} ${JAVA_HOME_PATH} ${RUN_MODE} 1024 --conf "spark.executor.memory=128g spark.driver.maxResultSize=64g spark.driver.memory=128g"
```
