package stream;

import org.apache.spark.api.java.function.Function;
import sparkjni.utils.CppSyntax;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniSingletonBuilder;
import testutils.TestUtils;
import utils.ExampleUtils;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StreamMain {
    private static DeployMode.DeployModes deployMode = DeployMode.DeployModes.FULL_GENERATE_AND_BUILD;
    private static int ARRAY_SIZE = 1<<22;
    private static int NUMBER_OF_ARRAYS = 4;

    public void main(String[] args) {
        TestUtils testUtils = new TestUtils(StreamMain.class);

        String nativePath = testUtils.initTestDir();;
        String appName = "stream";
        initSparkJNI(appName, nativePath);
        String libPath = String.format(CppSyntax.NATIVE_LIB_PATH, nativePath, appName);

        List<StreamVectors> input = generateVectors(ARRAY_SIZE, NUMBER_OF_ARRAYS);
        List<StreamVectors> duplicatedInput = duplicate(input);
        List<StreamVectors> nativeResults = performNativeStream(libPath, appName, input);
        System.out.println(String.format("Native %s millis", nativeResults.get(0).getTotalTimeNanos() / 1000000));
        List<StreamVectors> javaResults = performJavaStream(appName, duplicatedInput);
        System.out.println(String.format("Java %s millis", javaResults.get(0).getTotalTimeNanos() / 1000000));

        if(javaResults.equals(nativeResults))
            System.out.println("Succesful");
    }

    private static List<StreamVectors> duplicate(List<StreamVectors> input) {
        List<StreamVectors> duplicate = new ArrayList<>();
        for(int listIdx = 0; listIdx < input.size(); listIdx++){
            duplicate.add(
                    new StreamVectors(
                    Arrays.copyOf(input.get(listIdx).A, input.get(listIdx).A.length),
                    Arrays.copyOf(input.get(listIdx).B, input.get(listIdx).B.length),
                    Arrays.copyOf(input.get(listIdx).C, input.get(listIdx).C.length),
                    input.get(listIdx).scaling_constant)
            );
        }
        return duplicate;
    }

    private List<StreamVectors> performNativeStream(String libPath, String appName, List<StreamVectors> input) {
        return ExampleUtils.getSparkContext(appName).parallelize(input).map(new NativeStream(libPath, appName)).collect();
    }

    private List<StreamVectors> performJavaStream(String appName, List<StreamVectors> input) {
        return ExampleUtils.getSparkContext(appName).parallelize(input).map(new Function<StreamVectors, StreamVectors>() {
            @Override
            public StreamVectors call(StreamVectors streamVectors) throws Exception {
                streamVectors.setStartRun(System.nanoTime());
                for(int idx = 0; idx < streamVectors.A.length; idx++){
                    streamVectors.C[idx] = streamVectors.A[idx];
                }
                for(int idx = 0; idx < streamVectors.A.length; idx++){
                    streamVectors.B[idx] = streamVectors.scaling_constant * streamVectors.C[idx];
                }
                for(int idx = 0; idx < streamVectors.A.length; idx++){
                    streamVectors.C[idx] = streamVectors.A[idx] + streamVectors.B[idx];
                }
                for(int idx = 0; idx < streamVectors.A.length; idx++){
                    streamVectors.A[idx] = streamVectors.B[idx] + streamVectors.scaling_constant * streamVectors.C[idx];
                }
                streamVectors.setEndRun(System.nanoTime());
                return streamVectors;
            }
        }).collect();
    }

    private static List<StreamVectors> generateVectors(int arraySize, int numberOfArrays) {
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

    private static void initSparkJNI(String appName, String nativePath){
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
        String examplesClasspath = FileSystems.getDefault().getPath("target/classes").toAbsolutePath().normalize().toString();

        SparkJni sparkJni = new SparkJniSingletonBuilder()
                .nativePath(nativePath)
                .appName(appName)
                .build();

        sparkJni.setDeployMode(new DeployMode(deployMode))
                .addToClasspath(sparkjniClasspath, examplesClasspath);

        sparkJni.registerContainer(StreamVectors.class)
                .registerJniFunction(NativeStream.class);
        sparkJni.deployWithCodeInjections(codeInsertions);
    }
}