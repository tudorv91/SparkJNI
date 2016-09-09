package org.tudelft.ewi.ceng.examples.pairHMM;

import org.tudelft.ewi.ceng.sparkjni.utils.Bean;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_class;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_field;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_method;
import org.tudelft.ewi.ceng.sparkjni.annotations.JNI_param;

/**
 * Created by root on 8/14/16.
 */
@JNI_class public class PairHmmBean extends Bean {
    public WorkloadPairHmmBean getWorkload() {
        return workload;
    }

    public ByteArrBean getRawBufferBean() {
        return byteArrBean;
    }

    @JNI_field public double memcopyTime = 0;

    @JNI_field WorkloadPairHmmBean workload;
    @JNI_field ByteArrBean byteArrBean;

    @JNI_method public PairHmmBean(@JNI_param(target = "workload") WorkloadPairHmmBean workload,
                                   @JNI_param(target = "byteArrBean") ByteArrBean byteArrBean,
                                   @JNI_param(target = "memcopyTime") double memcopyTime) {
        this.workload = workload;
        this.byteArrBean = byteArrBean;
        this.memcopyTime = memcopyTime;
    }

    public PairHmmBean(@JNI_param(target = "workload") WorkloadPairHmmBean workload) {
        this.workload = workload;
    }

    public PairHmmBean(){
        workload = new WorkloadPairHmmBean();
    }
}
