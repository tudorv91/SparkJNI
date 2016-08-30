/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tudelft.ewi.ceng.examples.memcpyFramework;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.tudelft.ewi.ceng.JniFrameworkLoader;

import java.util.ArrayList;
import java.util.List;

public final class MemcpyKernel {
    public static void main(String[] args) throws Exception {
        int noOfBeans = 16;
        int noElemsPerBean = 1000;
        int count = 0;
        SparkConf sparkConf = new SparkConf().setAppName("JNI framework app");
        sparkConf.setMaster("local[8]");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
//        JavaSparkContext ctx = null;
        JniFrameworkLoader.setNativePath("/home/tudor/Desktop/Thesis/projects/framework/codeGen");
        JniFrameworkLoader.setDoWriteTemplateFile(false);
        JniFrameworkLoader.setSparkContext(ctx);
        JniFrameworkLoader.setJdkPath("/usr/lib/jvm/java-1.7.0-openjdk-amd64");
        JniFrameworkLoader.registerContainer(RawArrBean.class);
        JniFrameworkLoader.registerJniFunction(MemcpyJniFunction.class);
        JniFrameworkLoader.deploy("memcpy", "memcpy.cpp", ctx);

        ArrayList<RawArrBean> sample = new ArrayList<RawArrBean>();

        for(int i = 0; i < noOfBeans; i++) {
            double[] samples = new double[noElemsPerBean];
            for (int idx = 0; idx < noElemsPerBean; idx++) {
                samples[idx] = count++;
            }
            sample.add(new RawArrBean(samples, samples));
        }

        new MemcpyJniFunction<RawArrBean, RawArrBean>("memcpy.so", "callNativeMemcpy").call(sample.get(0));

        if(ctx == null)
            return;

        JavaRDD<RawArrBean> numberz = ctx.parallelize(sample);

        long start = System.currentTimeMillis();
        List<RawArrBean> processed = numberz.map(
                new MemcpyJniFunction<RawArrBean, RawArrBean>("memcpy.so", "callNativeMemcpy")
        ).collect();

        System.out.println((System.currentTimeMillis() - start)+" millis");
        ctx.stop();
    }
}
