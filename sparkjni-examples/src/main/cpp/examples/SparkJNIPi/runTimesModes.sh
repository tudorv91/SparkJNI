#!/bin/bash
cd /home/tudor/Desktop/SparkCL/spark-ucores/spark-2.0.1-bin-hadoop2.7
for noEx in `seq 0 0`
do
	for slices in `seq 0 2`
	do
		for sliceSize in `seq 0 5`
		do
			for idx in `seq 1 4`
			do
				slicesVal=$((2**$slices))
				sliceSizeVal=$((2**(10 + 3 *$sliceSize)))
				noexecs=$((2**$noEx))

				./bin/spark-submit --driver-memory 6G --class examples.sparkJNIPi.SparkJNIPi --master local[$noexecs] \
				--conf spark.executor.memory=6G /home/tudor/dev/SparkJNI/target/jni-spark-0.1.jar $slicesVal $sliceSizeVal $noexecs
			done
		done
	done
done
