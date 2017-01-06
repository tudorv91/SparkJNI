package pairhmm;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class PairHmmBenchmarkMain {
    static String modeString = "CPP";
    static String jdkPath = "/usr/lib/jvm/default-java";
    static String nativePath = "/home/tudor/dev/SparkJNI/sparkjni-examples/src/main/cpp/examples/pairhmm";
    static String nativeFuncName = "calculateSoftware";
    private static int noLines = 65536;
    static String appName = "pairhmm";

    public static void main(String[] args) {
        if(args.length >= 5){
            nativePath = args[0];
            appName = args[1];
            jdkPath = args[2];
            modeString = args[3];
            noLines = Integer.parseInt(args[4]);
        } else {
            System.out.println("Usage: <nativePath> <appName> <jdkPath> <CPP/FPGA> <NO_LINES>");
        }

        Options opt = new OptionsBuilder()
                .include(".*" + PairHmmBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .build();
        try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }
    }
}
