package org.tudelft.ewi.ceng.examples.pairHMM;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.tudelft.ewi.ceng.JniFrameworkLoader;
import scala.Tuple2;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by root on 8/13/16.
 */
public class PairHmmMain {
    private static String appName = "pairhmm";
    private static JavaSparkContext jscSingleton;
    private static String modeString = "CPP",
    jdkPath = "/usr/lib/jvm/java-1.7.0-openjdk-amd64",
            nativePath = "/home/tudor/Desktop/Thesis/projects/PairHMM_TACC",
            nativeFuncName = "calculateSoftware";
    private static int noLines = 32768;
    public static JavaSparkContext getSparkContext(){
        if(jscSingleton == null){
            SparkConf sparkConf = new SparkConf().setAppName(appName);
            sparkConf.setMaster("local[1]");
            jscSingleton = new JavaSparkContext(sparkConf);
        }
        return jscSingleton;
    }

    public static final long addressOffset;
    static {
        try {
            addressOffset = getUnsafe().objectFieldOffset(Buffer.class.getDeclaredField("address"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("restriction")
    public static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);

        } catch (RuntimeException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        if(args.length >= 5){
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
            modeString = args[3];
            noLines = Integer.parseInt(args[4]);
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath> <CPP/FPGA> <NO_LINES>");
        }

        if(modeString.equals("FPGA")) {
            JniFrameworkLoader.setUserIncludeDirs("/tools/ppc_64/libcxl");
            JniFrameworkLoader.setUserLibraryDirs("/tools/ppc_64/libcxl");
            JniFrameworkLoader.setUserStaticLibraries("/tools/ppc_64/libcxl/libcxl.a");
            JniFrameworkLoader.setUserDefines("FPGA_GO");
            nativeFuncName = "calculateHardware";
        } else {
            JniFrameworkLoader.setUserIncludeDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl");
            JniFrameworkLoader.setUserLibraryDirs("/home/tudor/capi-streaming-framework/sim/pslse/libcxl");
//            JniFrameworkLoader.setUserStaticLibraries("/home/tudor/capi-streaming-framework/sim/pslse/libcxl/libcxl.a");
        }

        JniFrameworkLoader.setJdkPath(jdkPath);
        JniFrameworkLoader.setNativePath(nativePath);
        JniFrameworkLoader.setDoGenerateMakefile(true);
        JniFrameworkLoader.setDoBuild(true);

        JniFrameworkLoader.registerJniFunction(PairHmmJniFunction.class);
        JniFrameworkLoader.registerJniFunction(LoadSizesJniFunction.class);
        JniFrameworkLoader.registerJniFunction(DataLoaderJniFunction.class);
        JniFrameworkLoader.registerContainer(WorkloadPairHmmBean.class);
        JniFrameworkLoader.registerContainer(PairHmmBean.class);
        JniFrameworkLoader.registerContainer(SizesBean.class);
        JniFrameworkLoader.registerContainer(ImageBean.class);
        JniFrameworkLoader.registerContainer(ByteArrBean.class);
        JniFrameworkLoader.deploy(appName, appName + ".cpp", null);
        String libPath = nativePath + "/pairhmm.so";

        appendToFile(String.valueOf(JniFrameworkLoader.getGenTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getJavahTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getBuildTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getLibLoadTime())+",");

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
        appendToFile(String.valueOf(totalActionTime)+",");
        appendToFile(String.valueOf(sparkResultPairHmm.getTotalTimeSeconds())+",");

        System.out.println(String.format("Hardware pairhmm done in %s ms", String.valueOf(totalActionTime)));

        int counter = 0;
        for(int x = 0; x < localRes.length; x+=16)
        {
            long sparkResult = 0L;
            sparkResult += ((long)(resultSpark[x+3] & 0xFF)) << 24;
            sparkResult += ((long)(resultSpark[x+2] & 0xFF)) << 16;
            sparkResult += ((long)(resultSpark[x+1] & 0xFF)) << 8;
            sparkResult += ((long)(resultSpark[x] & 0xFF));

            long softwareResult = 0L;
            softwareResult += ((long)(localRes[x+3] & 0xFF)) << 24;
            softwareResult += ((long)(localRes[x+2] & 0xFF)) << 16;
            softwareResult += ((long)(localRes[x+1] & 0xFF)) << 8;
            softwareResult += ((long)(localRes[x] & 0xFF));

            if(resultSpark[x] != localRes[x]) {
                System.out.println(String.format("Different at pos: %d. local vs Spark: %s vs %s", x,
                        Long.toHexString(softwareResult), Long.toHexString(sparkResult)));
                counter++;
            }
        }
        appendToFile(String.valueOf(counter));
        appendToFile("\n");

        System.out.println(String.format("Done for sizes %d", noLines));
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
            //more code
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
