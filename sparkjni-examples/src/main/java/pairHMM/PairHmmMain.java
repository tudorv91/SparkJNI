package pairHMM;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import sparkjni.utils.DeployTimesLogger;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tudor on 8/13/16.
 */
public class PairHmmMain {
    private static String appName = "pairhmm";
    private final static float ERROR_MARGIN_FLOAT = 0.00000001f;
    private static JavaSparkContext jscSingleton;
    private static String modeString = "CPP",
            jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64",
            nativePath = "/home/tudor/Desktop/Thesis/projects/PairHMM_TACC",
            nativeFuncName = "calculateSoftware";
    private static int noLines = 32768;
    private static SparkJni sparkJni;
    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster("local[1]");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    private static void initSparkJNI(String[] args){
        if(args.length >= 5){
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
            modeString = args[3];
            noLines = Integer.parseInt(args[4]);
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath> <CPP/FPGA> <NO_LINES>");
        }

        sparkJni = new SparkJniSingletonBuilder()
                .appName(appName)
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
        String libPath = nativePath + "/pairhmm.so";
        DeployTimesLogger deployTimesLogger = sparkJni.getDeployTimesLogger();
        appendToFile(String.valueOf(deployTimesLogger.getGenTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getJavahTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getBuildTime()/1000.0f)+",");
        appendToFile(String.valueOf(deployTimesLogger.getLibLoadTime()/1000.0f)+",");

        ArrayList<SizesBean> dummySizesCollection = new ArrayList<>();
        dummySizesCollection.add(loadSizes(nativePath+ "/sizes.txt", noLines));

        JavaRDD<SizesBean> sizesRDD = getSparkContext().parallelize(dummySizesCollection);
        JavaRDD<WorkloadPairHmmBean> workloadRDD = sizesRDD.map(new LoadSizesJniFunction(libPath, "loadSizes"));
        JavaRDD<ByteArrBean> dataInRdd = workloadRDD.map(new DataLoaderJniFunction(libPath, "callDataLoader"));
        JavaRDD<PairHmmBean> pairHmmBeanJavaRDD = dataInRdd.zip(workloadRDD).map(new Function<Tuple2<ByteArrBean, WorkloadPairHmmBean>, PairHmmBean>() {
            @Override
            public PairHmmBean call(Tuple2<ByteArrBean, WorkloadPairHmmBean> v1) throws Exception {
                return new PairHmmBean(v1._2(), v1._1(), 0);
            }
        });
        pairHmmBeanJavaRDD.cache();
        PairHmmBean pairHmmBean = pairHmmBeanJavaRDD.collect().get(0);

        byte[] localRes = runLocally(libPath, pairHmmBean);

        List<PairHmmBean> list = new ArrayList<>();
        list.add(pairHmmBean);
        JavaRDD<PairHmmBean> temp = getSparkContext().parallelize(list);
        PairHmmJniFunction pairHmmJniFunction = new PairHmmJniFunction(libPath, nativeFuncName);
        long start = System.currentTimeMillis();
        PairHmmBean sparkResultPairHmm = ((PairHmmBean)(temp.map(pairHmmJniFunction).collect().get(0)));
        long totalActionTime = System.currentTimeMillis() - start;

        byte[] resultSpark = sparkResultPairHmm.getRawBufferBean().arr;
        double kernelTime = sparkResultPairHmm.memcopyTime;

        appendToFile(String.valueOf(kernelTime)+",");
//        appendToFile(String.valueOf(totalActionTime)+",");
        appendToFile(String.valueOf(sparkResultPairHmm.getTotalTimeSeconds()));
        appendToFile("\n");
//        int counter = 0;
//        for(int x = 0; x < localRes.length; x+=16)
//        {
//            float sparkFloatRes = ByteBuffer.wrap(new byte[]{((byte)(resultSpark[x+3] & 0xFF)),
//                    ((byte)(resultSpark[x+2] & 0xFF)), ((byte)(resultSpark[x+1] & 0xFF)), ((byte)(resultSpark[x] & 0xFF))})
//                    .order(ByteOrder.LITTLE_ENDIAN).getFloat();
//
//            float cpuFloatRes = ByteBuffer.wrap(new byte[]{((byte)(localRes[x+3] & 0xFF)),
//                    ((byte)(localRes[x+2] & 0xFF)), ((byte)(localRes[x+1] & 0xFF)), ((byte)(localRes[x] & 0xFF))})
//                    .order(ByteOrder.LITTLE_ENDIAN).getFloat();
//
//            if(sparkFloatRes != cpuFloatRes) {
//                if(Math.abs(1-sparkFloatRes/cpuFloatRes) > ERROR_MARGIN_FLOAT) {
//                    System.out.println(String.format("ERROR at position: %d, local vs Spark: %f vs %f", x, cpuFloatRes, sparkFloatRes));
//                }
//                counter++;
//            }
//        }
//        appendToFile(String.valueOf(counter));
//        appendToFile("\n");

        System.out.println(String.format("Done for sizes %d", noLines));
        System.out.println(String.format("Hardware pairhmm done in %s ms", String.valueOf(totalActionTime)));
    }

    private static void createFile(){
        PrintWriter logger = null;

        try{
            logger = new PrintWriter(nativePath+"/resultsJava.csv", "UTF-8");
            logger.write("CodeGen time, javah time, build time, libload Time, kernel time, jFunction time\n");
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if(logger != null)
                logger.close();
        }
    }

    public static void appendToFile(String field){
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter printWriter = null;
        try{
            fw = new FileWriter(nativePath+"/resultsJava.csv", true);
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

    private static byte[] runLocally(String libPath, PairHmmBean bean){
        PairHmmJniFunction pairHmmJniFunction = new PairHmmJniFunction(libPath, "calculateSoftware");
        PairHmmBean resultSw = (PairHmmBean) pairHmmJniFunction.call(bean);
        return resultSw.getRawBufferBean().arr;
    }

    private static SizesBean loadSizes(String filePath, int noLines){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<Integer> col1 = new ArrayList<>();
        ArrayList<Integer> col2 = new ArrayList<>();

        int counter = 0;
        while(scanner.hasNextLine() && counter < noLines){
            String line = scanner.nextLine();
            String[] cols = line.split("\t");
            try{
                col1.add(Integer.parseInt(cols[0]));
                col2.add(Integer.parseInt(cols[1]));
                counter++;
            } catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        int[] col1Arr = new int[col1.size()];
        int[] col2Arr = new int[col1.size()];
        for(int i = 0; i < col1.size(); i++){
            col1Arr[i] = col1.get(i);
            col2Arr[i] = col2.get(i);
        }
        return new SizesBean(col1Arr, col2Arr);
    }
}
