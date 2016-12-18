package stream;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import utils.ExampleUtils;

import java.util.ArrayList;
import java.util.List;

public class StreamUtils {
    static List<StreamVectors> performJavaStream(String appName, List<StreamVectors> input, int noIters) {
        JavaRDD<StreamVectors> streamVectorsJavaRDD = ExampleUtils.getSparkContext(appName).parallelize(input);
        for (int i = 0; i < noIters; i++) {
            streamVectorsJavaRDD = streamVectorsJavaRDD.map(new Function<StreamVectors, StreamVectors>() {
                @Override
                public StreamVectors call(StreamVectors streamVectors) throws Exception {
                    streamVectors.setStartRun(System.nanoTime());
                    for (int idx = 0; idx < streamVectors.A.length; idx++) {
                        streamVectors.C[idx] = streamVectors.A[idx];
                    }
                    for (int idx = 0; idx < streamVectors.A.length; idx++) {
                        streamVectors.B[idx] = streamVectors.scaling_constant * streamVectors.C[idx];
                    }
                    for (int idx = 0; idx < streamVectors.A.length; idx++) {
                        streamVectors.C[idx] = streamVectors.A[idx] + streamVectors.B[idx];
                    }
                    for (int idx = 0; idx < streamVectors.A.length; idx++) {
                        streamVectors.A[idx] = streamVectors.B[idx] + streamVectors.scaling_constant * streamVectors.C[idx];
                    }
                    streamVectors.setEndRun(System.nanoTime());
                    return streamVectors;
                }
            });
        }
        return streamVectorsJavaRDD.collect();
    }

    static List<StreamVectors> generateVectors(int arraySize, int numberOfArrays) {
        List<StreamVectors> streamVectorsList = new ArrayList<>();
        for (int i = 0; i < numberOfArrays; i++) {
            double[] a = new double[arraySize];
            double[] b = new double[arraySize];
            double[] c = new double[arraySize];
            for (int idx = 0; idx < arraySize; idx++) {
                a[idx] = Math.random();
                b[idx] = Math.random();
                c[idx] = 0.0;
            }
            streamVectorsList.add(new StreamVectors(a, b, c, Math.random()));
        }
        return streamVectorsList;
    }

    static List<StreamVectors> performNativeStream(String libPath, String appName, List<StreamVectors> input, int noIters) {
        JavaRDD<StreamVectors> streamVectorsJavaRDD = ExampleUtils.getSparkContext(appName).parallelize(input);
        for(int idx = 0; idx < noIters; idx++) {
            streamVectorsJavaRDD = streamVectorsJavaRDD.map(new NativeStream(libPath, "stream"));
        }
        return streamVectorsJavaRDD.collect();
    }
}
