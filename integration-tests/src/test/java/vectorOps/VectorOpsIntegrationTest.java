package vectorOps;

import org.apache.spark.api.java.JavaRDD;
import org.junit.*;
import sparkjni.utils.DeployMode;
import sparkjni.utils.JniUtils;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class VectorOpsIntegrationTest {
    private static int noVectors = 64;
    private static int vectorSize = 64;
    private static ArrayList<VectorBean> vectorOfBeans;
    private static TestUtils testUtils;
    private static SparkJni sparkJni;

    @Before
    public void init() {
        testUtils = new TestUtils(VectorOpsIntegrationTest.class);
        testUtils.initTestDir();
        initSparkJNI();
        generateVectors();
    }

    public static void initSparkJNI() {
        DeployMode deployMode = new DeployMode(DeployMode.DeployModes.FULL_GENERATE_AND_BUILD);
        String sparkjniClasspath = FileSystems.getDefault().getPath("../core/target/classes").toAbsolutePath().normalize().toString();
        String classpath = FileSystems.getDefault().getPath("../sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String testClasspath = FileSystems.getDefault().getPath("target/test-classes").toAbsolutePath().normalize().toString();
        sparkJni = testUtils.getSparkJni(sparkjniClasspath + ":" + classpath + ":" + testClasspath).setDeployMode(deployMode);
        sparkJni.registerContainer(VectorBean.class);
        sparkJni.registerJniFunction(VectorMulJni.class)
                .registerJniFunction(VectorAddJni.class);
    }

    private static void generateVectors() {
        vectorOfBeans = new ArrayList<>();
        for (int i = 0; i < noVectors; i++) {
            int[] data = new int[vectorSize];
            for (int idx = 0; idx < vectorSize; idx++) {
                data[idx] = (int) (Math.random() * 1000);
            }
            vectorOfBeans.add(new VectorBean(data));
        }
    }

    private VectorBean computeLocal() {
        int[] res = new int[vectorSize];
        for (int vIdx = 0; vIdx < vectorSize; vIdx++) {
            res[vIdx] = 0;
            for (int idx = 0; idx < noVectors; idx++) {
                res[vIdx] += (vectorOfBeans.get(idx).data[vIdx] * 2);
            }
        }
        return new VectorBean(res);
    }

    @Test
    public void vectorOpsTest() {
        HashMap<String, String> codeInjections = new HashMap<>();
        codeInjections.put("reduceVectorAdd",
                "\tint vectorLength = cppvectorbean0->getdata_length();\n" +
                        "\tfor(int idx = 0; idx < vectorLength; idx++)\n" +
                        "\t\tcppvectorbean0->getdata()[idx] += cppvectorbean1->getdata()[idx];\n" +
                        "\treturn cppvectorbean0;\n");
        codeInjections.put("mapVectorMul",
                "\tint vectorLength = cppvectorbean0->getdata_length();\n" +
                        "\tfor(int idx = 0; idx < vectorLength; idx++)\n" +
                        "\t\tcppvectorbean0->getdata()[idx] *= 2;\n" +
                        "\treturn cppvectorbean0;\n");
        sparkJni.deployWithCodeInjections(codeInjections);
        String libPath = JniUtils.generateDefaultLibPath(testUtils.appName, testUtils.fullPath);
        JavaRDD<VectorBean> vectorsRdd = testUtils.getSparkContext().parallelize(vectorOfBeans);
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
        VectorBean expected = computeLocal();
        assertEquals(expected, results);
    }

    /**
     * Here we do not inject code into the kernel functions.
     * Should return default SparkJNI initialization values for fields in the bean classes.
     */
    @Test
    public void vectorOpsDefaultTest() {
        sparkJni.deploy();
        String libPath = JniUtils.generateDefaultLibPath(testUtils.appName, testUtils.fullPath);
        JavaRDD<VectorBean> vectorsRdd = testUtils.getSparkContext().parallelize(vectorOfBeans);
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
    }

    @After
    public void clean() {
        testUtils.cleanTestDir();
        sparkJni = null;
        vectorOfBeans = null;
    }
}