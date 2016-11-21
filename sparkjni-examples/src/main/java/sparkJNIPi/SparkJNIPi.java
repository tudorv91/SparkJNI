package sparkJNIPi;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static sparkjni.utils.DeployMode.DeployModes.JUST_BUILD;

public class SparkJNIPi {
    public static String appName = "SparkJNIPi";
    private static int slices = 8;
    private static int sliceSize = 10000000;
    private static int noExecs = 1;
    private static String logFilePath = "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi/SparkJNIPi.log";
    private static String nativeDirPath = "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi";
    private static JavaSparkContext jscSingleton;

    public static void main(String[] args) {
        String nativeLibPath = initSparkJni(args);

        long start = System.currentTimeMillis();
        List<Integer> input = getInputMocks(slices);
        JavaRDD<RandNumArray> piBeanJavaRDD = getSparkContext().parallelize(input).map(callSparkJNIPiNativeFunction);
        JavaRDD<SumArray> intermediateResult = piBeanJavaRDD.map(new RandOpenclPiMap(nativeLibPath, "randToSum"));
        SumArray result = intermediateResult.reduce(reduceCountPi);
        computePiAndLog(result, start);
    }

    private static String initSparkJni(String[] args) {
        slices = args.length > 0 ? Integer.parseInt(args[0]) : 4;
        sliceSize = args.length > 1 ? Integer.parseInt(args[1]) : 4194304;
        noExecs = args.length > 2 ? Integer.parseInt(args[2]) : 1;

        SparkJni sparkJni = new SparkJniSingletonBuilder()
                .appName(appName)
                .nativePath(nativeDirPath)
                .build();

        sparkJni.registerContainer(RandNumArray.class);
        sparkJni.registerContainer(SumArray.class);
        sparkJni.registerJniFunction(RandOpenclPiMap.class);
        sparkJni.setUserIncludeDirs("OpenCL");

        sparkJni.setDeployMode(new DeployMode(JUST_BUILD))
                .setSparkContext(getSparkContext())
                .deploy();

        return String.format("%s/%s.so", nativeDirPath, appName);
    }

    private static void computePiAndLog(SumArray result, long start){
        int sum = 0;
        for (int i = 0; i < sliceSize; i++) {
            sum += result.sum[i];
        }

        float PI = ((float) sum * 4.0f) / (sliceSize * slices);
        long end = System.currentTimeMillis();
        log((end - start) / 1000.0f, slices, sliceSize, String.format("local[%d]", noExecs));
        System.out.println("Result: " + PI + " in " + (end - start) / 1000.0f + " seconds");
    }

    private static List<Integer> getInputMocks(int slices) {
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < slices; i++)
            input.add(i);
        return input;
    }

    private static void log(Float duration, int noSlices, int sliceSize, String local) {
        String logLine = String.format("SparkJNIPi, %s, %d, %d, %s\n", local, noSlices, sliceSize, duration.toString());
        try {
            Files.write(Paths.get(logFilePath), logLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Function<Integer, RandNumArray> callSparkJNIPiNativeFunction = new Function<Integer, RandNumArray>() {
        @Override
        public RandNumArray call(Integer integer) throws Exception {
            float[] aux = new float[sliceSize * 2];
            for (int bIdx = 0; bIdx < sliceSize * 2; bIdx++) {
                aux[bIdx] = (float) Math.random();
            }
            return new RandNumArray(aux);
        }
    };

    private static Function2<SumArray, SumArray, SumArray> reduceCountPi = new Function2<SumArray, SumArray, SumArray>() {
        @Override
        public SumArray call(SumArray sum1, SumArray sum2) throws Exception {
            for (int idx = 0; idx < sum1.sum.length; idx++)
                sum1.sum[idx] += sum2.sum[idx];
            return sum1;
        }
    };

    public static JavaSparkContext getSparkContext() {
        if (jscSingleton == null) {
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster(String.format("local[%d]", noExecs))
                    .setExecutorEnv("spark.executor.memory", "4g").setExecutorEnv("spark.driver.memory", "4g").validateSettings();
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }
}
