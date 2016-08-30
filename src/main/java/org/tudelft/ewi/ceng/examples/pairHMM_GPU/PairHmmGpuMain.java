package org.tudelft.ewi.ceng.examples.pairHMM_GPU;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.tudelft.ewi.ceng.JniFrameworkLoader;

import java.io.*;
/**
 * Created by Tudor on 8/13/16.
 */
public class PairHmmGpuMain {
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

    public static void main(String[] args) throws Exception {
        if(args.length >= 3){
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath>");
        }

        JniFrameworkLoader.setJdkPath(jdkPath);
        JniFrameworkLoader.setNativePath(nativePath);
        JniFrameworkLoader.setDoGenerateMakefile(false);
        JniFrameworkLoader.setDoBuild(false);

        JniFrameworkLoader.registerJniFunction(PairHmmGpuJniFunction.class);

        JniFrameworkLoader.deploy(appName, appName + ".cu", null);
        String libPath = String.format("%s/%s.so", nativePath, appName);

        appendToFile(String.valueOf(JniFrameworkLoader.getGenTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getJavahTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getBuildTime())+",");
        appendToFile(String.valueOf(JniFrameworkLoader.getLibLoadTime())+",");

        System.load(libPath);

        PairHmmGpuJniFunction gpu = new PairHmmGpuJniFunction(libPath, "runContained");
        gpu.call(new Object());

        System.out.println("Done PairHMM GPU..");
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
}
