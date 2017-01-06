package pairHMM;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkEnv;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;
import sparkjni.utils.DeployMode;
import sparkjni.utils.DeployTimesLogger;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.io.*;
import java.util.*;

public class PairHmmMain {
    public static String appName = "pairhmm";
    private final static float ERROR_MARGIN_FLOAT = 0.00000001f;
    private static JavaSparkContext jscSingleton;
    public static String modeString = "CPP";
    public static String jdkPath = "/usr/lib/jvm/default-java";
    public static String nativePath = "/home/tudor/dev/SparkJNI/sparkjni-examples/src/main/cpp/examples/pairhmm";
    public static String nativeFuncName = "calculateSoftware";
    private static int noLines = 65536;
    private static SparkJni sparkJni;
    private static int noSplits = 4;
    private static String master = "spark://p8capi6:7077";

    public static JavaSparkContext getSparkContext() {
        if (jscSingleton == null) {
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            if (modeString.equals("CPP"))
                sparkConf.setMaster("local[1]");
            else
                sparkConf.setMaster(master);
            sparkConf.set( "spark.serializer", "org.apache.spark.serializer.KryoSerializer" );
            sparkConf.registerKryoClasses(new Class[]{PairHmmBean.class, SizesBean.class, WorkloadPairHmmBean.class, ByteArrBean.class});
            sparkConf.setIfMissing("spark.kryoserializer.buffer.max", "1g");
            sparkConf.validateSettings();
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    private static void initSparkJNI(String[] args) {
        if (args.length >= 5) {
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
            modeString = args[3];
            noLines = Integer.parseInt(args[4]);
            noSplits = Integer.parseInt(args[5]);
            master = args[6];
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath> <CPP/FPGA> <NO_LINES> <NO_OF_SPLITS> <MASTER>");
        }

        sparkJni = new SparkJniSingletonBuilder()
                .appName(appName)
                .jdkPath(jdkPath)
                .nativePath(nativePath)
                .build();

        if (modeString.equals("FPGA")) {
            sparkJni.setUserIncludeDirs("/tools/ppc_64/libcxl")
                    .setUserLibraryDirs("/tools/ppc_64/libcxl")
                    .setUserStaticLibraries("/tools/ppc_64/libcxl/libcxl.a")
                    .setUserDefines("FPGA_GO");
            nativeFuncName = "calculateHardware";
        } else {
            sparkJni.setUserIncludeDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl")
                    .setUserLibraryDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl");
        }

        sparkJni.setDeployMode(new DeployMode(DeployMode.DeployModes.JUST_BUILD));
        sparkJni.getJniHandler().setDEBUGGING_MODE(false);
        sparkJni.registerJniFunction(PairHmmJniFunction.class)
                .registerJniFunction(LoadSizesJniFunction.class)
                .registerJniFunction(DataLoaderJniFunction.class)
                .registerContainer(WorkloadPairHmmBean.class)
                .registerContainer(PairHmmBean.class)
                .registerContainer(SizesBean.class)
                .registerContainer(ByteArrBean.class)
                .deploy();
    }

    public static void main(String[] args) throws Exception {
        initSparkJNI(args);
        createFile();
        final String libPath = nativePath + "/pairhmm.so";
        DeployTimesLogger deployTimesLogger = sparkJni.getDeployTimesLogger();
        appendToFile(String.valueOf(deployTimesLogger.getGenTime() / 1000.0f) + ",");
        appendToFile(String.valueOf(deployTimesLogger.getJavahTime() / 1000.0f) + ",");
        appendToFile(String.valueOf(deployTimesLogger.getBuildTime() / 1000.0f) + ",");
        appendToFile(String.valueOf(deployTimesLogger.getLibLoadTime() / 1000.0f) + ",");

        ArrayList<SizesBean> dummySizesCollection = new ArrayList<>();
        dummySizesCollection.add(loadSizes(nativePath + "/sizes.txt", noLines));

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

//        Broadcast<List<PairHmmBean>> listBroadcast = getSparkContext().broadcast(pairHmmBeans);
//        processBroadCast(libPath, pairHmmBeans);

        processWithRDD(libPath, pairHmmBeans);
    }

    private static void processBroadCast(final String libPath, final Broadcast<List<PairHmmBean>> listBroadcast) {
        generateBlanksFor(noSplits).repartition(noSplits).map(new Function<Integer, PairHmmBean>() {
            @Override
            public PairHmmBean call(Integer integer) throws Exception {
                String executorId = SparkEnv.get().executorId();
                int execId = Character.getNumericValue(executorId.charAt(executorId.length() - 1));
                List<PairHmmBean> pairHmmBeans = listBroadcast.value();
                System.out.println(String.format("Processing element %d on executor %s", execId, executorId));
                if(execId >= pairHmmBeans.size())
                    return null;
                return new PairHmmJniFunction<PairHmmBean, PairHmmBean>(libPath, nativeFuncName).call(listBroadcast.value().get(execId));
            }
        }).collect();
    }

    private static JavaRDD<Integer> generateBlanksFor(int noSplits) {
        List<Integer> integers = new ArrayList<>();
        for(int i = 0; i < noSplits; i++) {
            integers.add(0);
        }
        return getSparkContext().parallelize(integers);
    }

    private static void processWithRDD(String libPath, List<PairHmmBean> pairHmmBeans) {
        JavaRDD<PairHmmBean> temp = getSparkContext().parallelize(pairHmmBeans);

        long start = System.currentTimeMillis();
        List<PairHmmBean> sparkResultPairHmm = temp.map(new PairHmmJniFunction<PairHmmBean, PairHmmBean>(libPath, nativeFuncName)).collect();
        long totalActionTime =  System.currentTimeMillis() - start;

        appendToFile(String.valueOf(totalActionTime)+",");
        System.out.println(String.format("Done for sizes %d", noLines));
        System.out.println(String.format("Hardware pairhmm done in %s ms", String.valueOf(totalActionTime)));
    }

    public static void createFile() {
        PrintWriter logger = null;

        try {
            logger = new PrintWriter(nativePath + "/resultsJava.csv", "UTF-8");
            logger.write("CodeGen time, javah time, build time, libload Time, kernel time, jFunction time\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (logger != null)
                logger.close();
        }
    }

    public static void appendToFile(String field) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter printWriter = null;
        try {
            fw = new FileWriter(nativePath + "/resultsJava.csv", true);
            bw = new BufferedWriter(fw);
            printWriter = new PrintWriter(bw);
            printWriter.print(field);
            System.out.print(field);

            printWriter.close();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static byte[] runLocally(String libPath, List<PairHmmBean> bean) {

    public static SizesBean loadSizes(String filePath, int noLines) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Integer> col1 = new ArrayList<>();
        ArrayList<Integer> col2 = new ArrayList<>();

        int counter = 0;
        while (scanner.hasNextLine() && counter < noLines) {
            String line = scanner.nextLine();
            String[] cols = line.split("\t");
            try {
                col1.add(Integer.parseInt(cols[0]));
                col2.add(Integer.parseInt(cols[1]));
                counter++;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        int[] col1Arr = new int[col1.size()];
        int[] col2Arr = new int[col1.size()];
        for (int i = 0; i < col1.size(); i++) {
            col1Arr[i] = col1.get(i);
            col2Arr[i] = col2.get(i);
        }
        return new SizesBean(col1Arr, col2Arr);
    }

    public static final FlatMapFunction<Iterator<SizesBean>, SizesBean> SPLIT_INPUT_FUNCTION = new FlatMapFunction<Iterator<SizesBean>, SizesBean>() {
        @Override
        public Iterator<SizesBean> call(Iterator<SizesBean> sizesBeanIterator) throws Exception {
            if (sizesBeanIterator.hasNext()) {
                SizesBean sizesBean = sizesBeanIterator.next();
                int[] col1 = sizesBean.col1, col2 = sizesBean.col2;
                int splitLength = col1.length / noSplits;
                if(col1.length != col2.length)
                    throw new RuntimeException(String.format("Columns lengths are different %d vs %d", col1.length, col2.length));
                List<SizesBean> sizesBeanList = new ArrayList<>();
                for (int idx = 0; idx < noSplits; idx++) {
                    int[] col1_aux = new int[splitLength];
                    int[] col2_aux = new int[splitLength];
                    System.arraycopy(col1, idx * splitLength, col1_aux, 0, splitLength);
                    System.arraycopy(col2, idx * splitLength, col2_aux, 0, splitLength);
                    sizesBeanList.add(new SizesBean(col1_aux, col2_aux));
                }
                return sizesBeanList.iterator();
            }
            return Collections.emptyIterator();
        }
    };
}