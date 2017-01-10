package memoryCopy;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;

import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MemoryCopyTest {
    private static final int NO_SLICES_ITERATIVE = 1<<4;
    private static final int SLICE_COPY_SIZE_ITERATIVE = 1<<16;
    private TestUtils testUtils;
    private SparkJni sparkJni;
    private final String ARRAY_COPY_CODE_BODY =
            "\tint arraySize = cppdoublearray0->getdblArr_length();\n" +
                    "\tdouble *arr = new double[arraySize];\n" +
                    "\tmemcpy(arr, cppdoublearray0->getdblArr(), arraySize * sizeof(double));\n" +
                    "\tstd::shared_ptr<CPPDoubleArray> retObj = std::make_shared<CPPDoubleArray>(arr, arraySize, cppdoublearray_jClass, jniEnv);\n" +
                    "\tdelete arr;\n" +
                    "\rreturn retObj;\n";

    @Before
    public void init() {
        String sparkjniClasspath = FileSystems.getDefault().getPath("../core/target/classes").toAbsolutePath().normalize().toString();
        String classpath = FileSystems.getDefault().getPath("../sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String testClasspath = FileSystems.getDefault().getPath("target/test-classes").toAbsolutePath().normalize().toString();

        HashMap<String, String> codeInjection = new HashMap<>();
        codeInjection.put("arrayCopy", ARRAY_COPY_CODE_BODY);

        testUtils = new TestUtils(MemoryCopyTest.class);
        sparkJni = testUtils.getSparkJni(sparkjniClasspath + ":" + classpath + ":" + testClasspath);
        sparkJni.registerContainer(DoubleArray.class);
        sparkJni.registerJniFunction(MapCopyFunc.class);
        sparkJni.deployWithCodeInjections(codeInjection);
    }

    @Test
    public void simpleMemoryCopyTest() {
        int copySize = 65536;
        int noSlices = 1;
        run(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    @Test
    public void multiThreadedMemoryCopyTest() {
        int copySize = 65536;
        int noSlices = 256;
        run(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    @Test
    public void bigObjectsMemoryCopyTest() {
        int copySize = 1<<24;
        int noSlices = 1;
        run(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    @Test
    public void bigNoOfSlicesMemoryCopyTest() {
        int copySize = 1;
        int noSlices = 1<<16;
        run(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    @Test
    public void sparkIterativeMemoryTest(){
        int copySize = SLICE_COPY_SIZE_ITERATIVE;
        int noSlices = NO_SLICES_ITERATIVE;
        runSparkIterative(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    @Test
    public void iterativeMemoryTest(){
        int copySize = SLICE_COPY_SIZE_ITERATIVE;
        int noSlices = NO_SLICES_ITERATIVE;
        runIterative(MemoryCopyUtil.generateArray(copySize, noSlices));
    }

    private void runSparkIterative(List<DoubleArray> input) {
        List<DoubleArray> result;
        JavaRDD<DoubleArray> rdd = testUtils.getSparkContext().parallelize(input);
        for(int i = 0; i < 1<<8; i++) {
            rdd = rdd.map(MEMCOPY_FUNCTION);
        }
        result = rdd.collect();
        Assert.assertEquals(result, input);
    }

    @SuppressWarnings (value = "Unchecked")
    private void run(List<DoubleArray> input){
        List<DoubleArray> result = testUtils.getSparkContext().parallelize(input)
                .map(new MapCopyFunc(testUtils.getLibPath(), "arrayCopy")).collect();
        Assert.assertEquals(result, input);
    }

    @SuppressWarnings (value = "Unchecked")
    private void runIterative(List<DoubleArray> input) {
        List<DoubleArray> result;
        JavaRDD<DoubleArray> rdd = testUtils.getSparkContext().parallelize(input);
        for(int i = 0; i < 1<<8; i++) {
            rdd = rdd.map(new MapCopyFunc(testUtils.getLibPath(), "arrayCopy"));
        }
        result = rdd.collect();
        Assert.assertEquals(result, input);
    }

    @After
    public void cleanup(){
        SparkJni.reset();
        testUtils.cleanTestDir();
    }

    final static Function<DoubleArray, DoubleArray> MEMCOPY_FUNCTION = new Function<DoubleArray, DoubleArray>() {
        @Override
        public DoubleArray call(DoubleArray doubleArray) throws Exception {
            double[] array = Arrays.copyOf(doubleArray.dblArr, doubleArray.dblArr.length);
//            for(int idx = 0; idx < doubleArray.dblArr.length; idx++)
//                array[idx] = doubleArray.dblArr[idx];
            return new DoubleArray(array);
        }
    };
}
