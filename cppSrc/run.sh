#!/bin/bash
cd /home/04168/tudorv91/spark-2.0.0-bin-hadoop2.7
./bin/spark-submit --driver-java-options "-XX:+ShowMessageBoxOnError" --class org.tudelft.ewi.ceng.examples.pairHMM.PairHmmMain ../pairHMM_v0.9/jni-spark-0.1.jar /home/04168/tudorv91/pairHMM_v0.9 pairhmm /usr/lib/jvm/default-java FPGA $1 --conf "spark.executor.memory=128g spark.driver.maxResultSize=64g spark.driver.memory=128g"
