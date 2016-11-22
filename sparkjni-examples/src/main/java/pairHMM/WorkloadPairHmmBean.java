package pairHMM;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_field;
import sparkjni.utils.jniAnnotations.JNI_method;
import sparkjni.utils.jniAnnotations.JNI_param;
import sparkjni.dataLink.JavaBean;

import java.io.Serializable;

/**
 * Created by Tudor on 8/16/16.
 */
@JNI_class
public class WorkloadPairHmmBean extends JavaBean implements Serializable {
    public WorkloadPairHmmBean(){}

    // Fields should not be left uninitialized! Leads to crash in JNI
    @JNI_field
    public int pairs = 0;
    @JNI_field public int batches = 0;
    @JNI_field(nativeTypeTarget = "uint16_t*") int[] hapl ={0}, read={0};
    @JNI_field(nativeTypeTarget = "uint32_t*") int[] bx={0}, by={0};
    @JNI_field(nativeTypeTarget = "size_t*") int[] bbytes={0};
    @JNI_field(nativeTypeTarget = "size_t") int bytes = 0;

    @JNI_method() public WorkloadPairHmmBean(@JNI_param(target = "pairs") int pairs, @JNI_param(target = "batches") int batches,
                                             @JNI_param(target = "hapl") int[] hapl, @JNI_param(target = "read") int[] read,
                                             @JNI_param(target = "bx") int[] bx, @JNI_param(target = "by") int[] by,
                                             @JNI_param(target = "bbytes") int[] bbytes, @JNI_param(target = "bytes") int bytes) {
        this.pairs = pairs;
        this.batches = batches;
        this.hapl = hapl;
        this.read = read;
        this.bx = bx;
        this.by = by;
        this.bbytes = bbytes;
        this.bytes = bytes;
    }
}
