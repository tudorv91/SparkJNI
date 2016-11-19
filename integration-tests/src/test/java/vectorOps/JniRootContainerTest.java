package vectorOps;

import org.junit.*;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;

import java.util.ArrayList;
import java.util.List;

public class JniRootContainerTest {
    private static int noVectors = 64;
    private static int vectorSize = 64;
    private static ArrayList<VectorBean> vectorOfBeans;
    private static TestUtils testUtils;
    private static SparkJni sparkJni;

    @Before
    public void init(){
        testUtils = new TestUtils(JniRootContainerTest.class);
        testUtils.initTestDir();
        Assert.assertNotNull(testUtils.jdkPath);
        initSparkJNI();
        generateVectors();
    }

    public static void initSparkJNI(){
        sparkJni = testUtils.getSparkJni();
        sparkJni.registerContainer(VectorBean.class);
        sparkJni.registerJniFunction(VectorMulJni.class)
                .registerJniFunction(VectorAddJni.class)
                .deploy();
    }

    private static void generateVectors(){
        vectorOfBeans = new ArrayList<>();
        for(int i = 0; i < noVectors; i++){
            int[] data = new int[vectorSize];
            for(int idx = 0; idx < vectorSize; idx++) {
                data[idx] = (int) (Math.random() * 1000);
            }
            vectorOfBeans.add(new VectorBean(data));
        }
    }

    @Ignore
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
    public void clean(){
        testUtils.cleanTestDir();
    }
}