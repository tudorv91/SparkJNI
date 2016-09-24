package org.tudelft.ewi.ceng.examples.vectorOps;

import org.apache.spark.api.java.JavaRDD;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tudelft.ewi.ceng.TestUtils;
import org.tudelft.ewi.ceng.sparkjni.utils.SparkJni;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by root on 9/21/16.
 */
public class VectorOpsMainTest {
    private static int noVectors = 64;
    private static int vectorSize = 64;
    private static ArrayList<VectorBean> vectorOfBeans;
    private TestUtils testUtils;

    @Before
    public void init(){
        testUtils = new TestUtils("vectorOpsTest");
        testUtils.initTestDir();
        assertNotNull(testUtils.jdkPath);
        initSparkJNI();
        generateVectors();
    }

    public void initSparkJNI(){
        SparkJni.setJdkPath(testUtils.jdkPath);
        SparkJni.setNativePath(testUtils.defaultTestFolder);
        SparkJni.setDoGenerateMakefile(true);
        SparkJni.setDoBuild(true);

        SparkJni.registerContainer(VectorBean.class);
        SparkJni.registerJniFunction(VectorMulJni.class);
        SparkJni.registerJniFunction(VectorAddJni.class);
        SparkJni.deploy(testUtils.appName, null);
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
    public void vectorOpsTest(){
        String libPath = String.format("%s/%s.so", testUtils.defaultTestFolder, testUtils.appName);
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