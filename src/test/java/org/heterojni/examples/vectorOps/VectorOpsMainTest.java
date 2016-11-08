package org.heterojni.examples.vectorOps;

import com.google.common.base.Optional;
import org.apache.spark.api.java.JavaRDD;
import org.heterojni.TestUtils;
import org.heterojni.sparkjni.jniLink.linkContainers.JniHeader;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;
import org.heterojni.sparkjni.utils.JniUtils;
import org.heterojni.sparkjni.utils.SparkJniSingletonBuilder;
import org.junit.*;
import org.heterojni.sparkjni.utils.SparkJni;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by root on 9/21/16.
 */
public class VectorOpsMainTest {
    private static int noVectors = 64;
    private static int vectorSize = 64;
    private static ArrayList<VectorBean> vectorOfBeans;
    private static TestUtils testUtils;
    private static SparkJni sparkJni;

    @Before
    public void init(){
        testUtils = new TestUtils(VectorOpsMainTest.class);
        testUtils.initTestDir();
        assertNotNull(testUtils.jdkPath);
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

    private VectorBean computeLocal(){
        int[] res = new int[vectorSize];
        for(int vIdx = 0; vIdx < vectorSize; vIdx++) {
            res[vIdx] = 0;
            for (int idx = 0; idx < noVectors; idx++) {
                res[vIdx] += vectorOfBeans.get(idx).data[vIdx];
            }
        }
        return new VectorBean(res);
    }

    @Test
    public void jniRootContainerTest(){
        JniRootContainer jniRootContainer = sparkJni.getJniRootContainer();
        assertEquals(testUtils.appName, jniRootContainer.appName());

        List<JniHeader> jniHeaders = jniRootContainer.jniHeaders();
        assertEquals(2, jniHeaders.size());

        for(JniHeader jniHeader: jniHeaders){
            assertTrue(jniHeader.jniHeaderFile().exists());
        }
    }

    @Test
    public void vectorOpsTest(){
        String libPath = JniUtils.generateDefaultLibPath(testUtils.appName, testUtils.fullPath);
        JavaRDD<VectorBean> vectorsRdd = testUtils.getSparkContext().parallelize(vectorOfBeans);
        JavaRDD<VectorBean> mulResults = vectorsRdd.map(new VectorMulJni(libPath, "mapVectorMul"));
        VectorBean results = mulResults.reduce(new VectorAddJni(libPath, "reduceVectorAdd"));
        assertEquals(results, computeLocal());
    }

    @After
    public void clean(){
        testUtils.cleanTestDir();
    }
}