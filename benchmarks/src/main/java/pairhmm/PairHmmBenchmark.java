package pairhmm;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import pairHMM.*;
import scala.Tuple2;
import sparkjni.utils.DeployTimesLogger;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pairHMM.PairHmmMain.*;
import static pairhmm.PairHmmBenchmarkMain.appName;
import static pairhmm.PairHmmBenchmarkMain.jdkPath;
import static pairhmm.PairHmmBenchmarkMain.modeString;
import static pairhmm.PairHmmBenchmarkMain.nativeFuncName;
import static pairhmm.PairHmmBenchmarkMain.nativePath;

@State(Scope.Benchmark)
@BenchmarkMode(value = Mode.SampleTime)
@Warmup(iterations = 3)
@Fork (3)
@Measurement(timeUnit = TimeUnit.MILLISECONDS, iterations = 5)
public class PairHmmBenchmark {
    private static SparkJni sparkJni;

    @Param(value = {"" + (1 << 10), "" + (1 << 12), "" + (1 << 14), "" + (1 << 16)})
    private static int noLines;
    @Param(value = {"1", "2", "4"})
    private static int noExecs;

    private static final ArrayList<SizesBean> DUMMY_SIZES_COLLECTION = new ArrayList<>();
    private static final String LIB_PATH = nativePath + "/pairhmm.so";
    private static JavaSparkContext jscSingleton;

    @Setup
    public static void setup(){
        createFile();
        initSparkJni();
        DeployTimesLogger deployTimesLogger = sparkJni.getDeployTimesLogger();
        appendToFile(String.valueOf(deployTimesLogger.getGenTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getJavahTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getBuildTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getLibLoadTime()/1000.0f)+",");
        DUMMY_SIZES_COLLECTION.add(loadSizes(nativePath+ "/sizes.txt", noLines));
    }

    @Benchmark
    public static void main(Blackhole bh) throws Exception {
        JavaRDD<SizesBean> sizesRDD = getSparkContext().parallelize(DUMMY_SIZES_COLLECTION).mapPartitions(PairHmmMain.SPLIT_INPUT_FUNCTION);
        JavaRDD<WorkloadPairHmmBean> workloadRDD = sizesRDD.map(new LoadSizesJniFunction(LIB_PATH, "loadSizes"));
        JavaRDD<ByteArrBean> dataInRdd = workloadRDD.map(new DataLoaderJniFunction(LIB_PATH, "callDataLoader"));
        JavaRDD<PairHmmBean> pairHmmBeanJavaRDD = dataInRdd.zip(workloadRDD).map(new Function<Tuple2<ByteArrBean, WorkloadPairHmmBean>, PairHmmBean>() {
            @Override
            public PairHmmBean call(Tuple2<ByteArrBean, WorkloadPairHmmBean> v1) throws Exception {
                return new PairHmmBean(v1._2(), v1._1(), 0);
            }
        });
        pairHmmBeanJavaRDD.cache();
        List<PairHmmBean> pairHmmBeans = pairHmmBeanJavaRDD.collect();

        JavaRDD<PairHmmBean> temp = getSparkContext().parallelize(pairHmmBeans);

        long start = System.currentTimeMillis();
        bh.consume(temp.map(new PairHmmJniFunction(LIB_PATH, nativeFuncName)).collect());
        long totalActionTime = System.currentTimeMillis() - start;

        System.out.println(String.format("Done for sizes %d", noLines));
        System.out.println(String.format("Hardware pairhmm done in %s ms", String.valueOf(totalActionTime)));
    }

    private static void initSparkJni(){
        sparkJni = new SparkJniSingletonBuilder()
                .appName(PairHmmMain.appName)
                .jdkPath(jdkPath)
                .nativePath(nativePath)
                .build();

        if(modeString.equals("FPGA")) {
            sparkJni.setUserIncludeDirs("/tools/ppc_64/libcxl")
                    .setUserLibraryDirs("/tools/ppc_64/libcxl")
                    .setUserStaticLibraries("/tools/ppc_64/libcxl/libcxl.a")
                    .setUserDefines("FPGA_GO");
            nativeFuncName = "calculateHardware";
        } else {
            sparkJni.setUserIncludeDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl")
                    .setUserLibraryDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl");
        }
    }

    private static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster(String.format("local[%d]", noExecs));
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }
}
