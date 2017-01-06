import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.openjdk.jmh.annotations.*;
import pairHMM.*;
import scala.Tuple2;
import sparkjni.utils.DeployTimesLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pairHMM.PairHmmMain.*;

@State(Scope.Benchmark)
@BenchmarkMode(value = Mode.SampleTime)
@Warmup(iterations = 3)
@Fork (3)
@Measurement(timeUnit = TimeUnit.MILLISECONDS, iterations = 5)
public class PairHmmBenchmark {
    @Param(value = {"" + (1 << 10)})
    private static int noLines;
    public static void main(String[] args) throws Exception {
        initSparkJNI(args);
        createFile();
        String libPath = nativePath + "/pairhmm.so";
        DeployTimesLogger deployTimesLogger = sparkJni.getDeployTimesLogger();
        appendToFile(String.valueOf(deployTimesLogger.getGenTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getJavahTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getBuildTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getLibLoadTime()/1000.0f)+",");

        ArrayList<SizesBean> dummySizesCollection = new ArrayList<>();
        dummySizesCollection.add(loadSizes(nativePath+ "/sizes.txt", noLines));

        JavaRDD<SizesBean> sizesRDD = getSparkContext().parallelize(dummySizesCollection).mapPartitions(SPLIT_INPUT_FUNCTION);
        JavaRDD<WorkloadPairHmmBean> workloadRDD = sizesRDD.map(new LoadSizesJniFunction(libPath, "loadSizes"));
        JavaRDD<ByteArrBean> dataInRdd = workloadRDD.map(new DataLoaderJniFunction(libPath, "callDataLoader"));
        JavaRDD<PairHmmBean> pairHmmBeanJavaRDD = dataInRdd.zip(workloadRDD).map(new Function<Tuple2<ByteArrBean, WorkloadPairHmmBean>, PairHmmBean>() {
            @Override
            public PairHmmBean call(Tuple2<ByteArrBean, WorkloadPairHmmBean> v1) throws Exception {
                return new PairHmmBean(v1._2(), v1._1(), 0);
            }
        });
        pairHmmBeanJavaRDD.cache();
        List<PairHmmBean> pairHmmBeans = pairHmmBeanJavaRDD.collect();

//        byte[] localRes = runLocally(libPath, pairHmmBeans);

        JavaRDD<PairHmmBean> temp = getSparkContext().parallelize(pairHmmBeans);

        long start = System.currentTimeMillis();
        List<PairHmmBean> sparkResultPairHmm = temp.map(new PairHmmJniFunction(libPath, nativeFuncName)).collect();
        long totalActionTime = System.currentTimeMillis() - start;

        System.out.println(String.format("Done for sizes %d", noLines));
        System.out.println(String.format("Hardware pairhmm done in %s ms", String.valueOf(totalActionTime)));
    }

}
