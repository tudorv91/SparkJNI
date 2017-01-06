package pairHMM;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.dataLink.JavaBean;

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
