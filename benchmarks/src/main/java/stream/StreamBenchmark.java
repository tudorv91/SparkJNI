package stream;

import org.apache.log4j.*;
import org.apache.log4j.Level;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;
import testutils.TestUtils;
import utils.ExampleUtils;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State (Scope.Benchmark)
@BenchmarkMode(value = Mode.SampleTime)
@Warmup(iterations = 3)
@Fork (3)
@Measurement(timeUnit = TimeUnit.MILLISECONDS, iterations = 5)
public class StreamBenchmark {
    private String appName;
    private String libPath;
    private SparkJni sparkJni;

    @Param ({"" + (1 << 18), "" + (1 << 21), "" + (1 << 24)})
    private static int ARRAY_SIZE;
    @Param ({"1"})
    private static int NUMBER_OF_ARRAYS;
    @Param ({"1", "4", "16", "64"})
    private static int NO_ITERS_STREAM;

    private static List<StreamVectors> input;

    private DeployMode.DeployModes deployMode = DeployMode.DeployModes.FULL_GENERATE_AND_BUILD;

    private void init() {
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        TestUtils testUtils = new TestUtils(StreamBenchmark.class);
        appName = testUtils.appName;
        String nativePath = testUtils.initTestDir();
        libPath = String.format(CppSyntax.NATIVE_LIB_PATH, nativePath, appName);
        sparkJni = new SparkJniSingletonBuilder()
                .nativePath(nativePath)
                .appName(appName)
                .build();
        sparkJni.setJdkPath("/usr/lib/jvm/java-1.7.0-openjdk-ppc64el");
        sparkJni.getJniHandler().setDEBUGGING_MODE(false);
        ExampleUtils.getSparkContext(appName);
        initSparkJNI();
    }

    @Setup
    public void setup(){
        input = StreamUtils.generateVectors(ARRAY_SIZE, NUMBER_OF_ARRAYS);
        init();
    }

    @Benchmark
    public void mainJava(Blackhole blackhole) {
        blackhole.consume(StreamUtils.performJavaStream(appName, input, NO_ITERS_STREAM));
    }

    @Benchmark
    public void mainNative(Blackhole blackhole) {
        blackhole.consume(StreamUtils.performNativeStream(libPath, appName, input, NO_ITERS_STREAM));
    }

    private void initSparkJNI() {
        HashMap<String, String> codeInsertions = new HashMap<>();
        codeInsertions.put("stream", "\tint length = cppstreamvectors0->getA_length();\n" +
                "\tdouble *A = cppstreamvectors0->getA();\n" +
                "\tdouble *B = cppstreamvectors0->getB();\n" +
                "\tdouble *C = cppstreamvectors0->getC();\n" +
                "\tdouble scaling_constant = cppstreamvectors0->getscaling_constant();\n" +
                "\tfor(int idx = 0; idx < length; idx++){\n" +
                "\t    C[idx] = A[idx];\n" +
                "\t}\n" +
                "    for(int idx = 0; idx < length; idx++){\n" +
                "        B[idx] = C[idx] * scaling_constant;\n" +
                "    }\n" +
                "    for(int idx = 0; idx < length; idx++){\n" +
                "        C[idx] = A[idx] + B[idx];\n" +
                "    }\n" +
                "    for(int idx = 0; idx < length; idx++){\n" +
                "        A[idx] = B[idx] + scaling_constant * C[idx];\n" +
                "    }\n" +
                "    return cppstreamvectors0;\n");

        String sparkjniClasspath = FileSystems.getDefault().getPath("../core/target/classes").toAbsolutePath().normalize().toString();
        String examplesClasspath = FileSystems.getDefault().getPath("../sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String thisClasspath = FileSystems.getDefault().getPath("target/classes").toAbsolutePath().normalize().toString();
        String thisJarClasspath = FileSystems.getDefault().getPath("sparkjni-benchmarks.jar").toAbsolutePath().normalize().toString();

        sparkJni.setDeployMode(new DeployMode(deployMode))
                .addToClasspath(sparkjniClasspath, examplesClasspath, thisClasspath, thisJarClasspath);

        sparkJni.registerContainer(StreamVectors.class)
                .registerJniFunction(NativeStream.class);
        sparkJni.deployWithCodeInjections(codeInsertions);
    }
}