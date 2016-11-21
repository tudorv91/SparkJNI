package sparkjni.jniLink.linkContainers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;
import unitTestUtils.VectorAddJni;
import unitTestUtils.VectorBean;
import unitTestUtils.VectorMulJni;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

public class JniRootContainerTest {
    private static TestUtils testUtils;
    private static SparkJni sparkJni;

    @Before
    public void init() {
        testUtils = new TestUtils(JniRootContainerTest.class);
        testUtils.initTestDir();
        initSparkJNI();
        generateVectors();
    }

    private static void initSparkJNI() {
        String sparkjniClasspath = FileSystems.getDefault().getPath("../core/target/classes").toAbsolutePath().normalize().toString();
        String classpath = FileSystems.getDefault().getPath("../sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String testClasspath = FileSystems.getDefault().getPath("target/test-classes").toAbsolutePath().normalize().toString();
        sparkJni = testUtils.getSparkJni(sparkjniClasspath + ":" + classpath + ":" + testClasspath);
        sparkJni.registerContainer(VectorBean.class);
        sparkJni.registerJniFunction(VectorMulJni.class)
                .registerJniFunction(VectorAddJni.class)
                .deploy();
    }

    private static void generateVectors() {
        ArrayList<VectorBean> vectorOfBeans = new ArrayList<>();
        int noVectors = 64;
        int vectorSize = 64;
        for (int i = 0; i < noVectors; i++) {
            int[] data = new int[vectorSize];
            for (int idx = 0; idx < vectorSize; idx++) {
                data[idx] = (int) (Math.random() * 1000);
            }
            vectorOfBeans.add(new VectorBean(data));
        }
    }

    @Test
    public void jniRootContainerTest() {
        sparkJni.deploy();
        JniRootContainer jniRootContainer = sparkJni.getJniRootContainer();
        Assert.assertEquals(testUtils.appName, jniRootContainer.appName());

        List<JniHeader> jniHeaders = jniRootContainer.jniHeaders();
        Assert.assertEquals(2, jniHeaders.size());

        for (JniHeader jniHeader : jniHeaders) {
            Assert.assertTrue(jniHeader.jniHeaderFile().exists());
        }
    }

    @After
    public void clean() {
        testUtils.cleanTestDir();
    }
}