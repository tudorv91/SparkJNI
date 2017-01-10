package generator.packageTwo;

import generator.packageOne.VectorBean;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Before;
import org.junit.Test;
import testutils.TestUtils;

import java.util.List;

import static utils.ExampleUtils.generateVectors;

public class GeneratorTests {
    private TestUtils testUtils;

    @Before
    public void init() {
        testUtils = new TestUtils(GeneratorTests.class);
    }

    @Test
    public void runGeneratedCode() {
        JavaSparkContext javaSparkContext = testUtils.getSparkContext();
        List<VectorBean> vectorBeans = generateVectors(2, 1000, false);
    }
}
