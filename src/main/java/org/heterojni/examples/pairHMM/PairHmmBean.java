package org.heterojni.examples.pairHMM;

import org.heterojni.sparkjni.annotations.JNI_field;
import org.heterojni.sparkjni.annotations.JNI_method;
import org.heterojni.sparkjni.annotations.JNI_param;
import org.heterojni.sparkjni.annotations.JNI_class;
import org.heterojni.sparkjni.utils.JavaBean;

/**
 * Created by root on 8/14/16.
 */
@JNI_class
public class PairHmmBean extends JavaBean {
    public WorkloadPairHmmBean getWorkload() {
        return workload;
    }

    public ByteArrBean getRawBufferBean() {
        return byteArrBean;
    }

    @JNI_field
    public double memcopyTime = 0;

    @JNI_field WorkloadPairHmmBean workload;
    @JNI_field ByteArrBean byteArrBean;

    @JNI_method
    public PairHmmBean(@JNI_param(target = "workload") WorkloadPairHmmBean workload,
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
