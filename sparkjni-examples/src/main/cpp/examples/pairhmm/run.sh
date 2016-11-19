#!/bin/bash
cd ${SPARK_HOME}
./bin/spark-submit --driver-java-options "-XX:+ShowMessageBoxOnError" --class PairHmmMain ${SPARK_JNI}/target/jni-spark-0.1.jar $1 $2 ${JAVA_HOME} FPGA $3 --conf "spark.executor.memory=4g spark.driver.maxResultSize=4g spark.driver.memory=4g"
