package org.tudelft.ewi.ceng.examples.memcpyFramework;

import org.tudelft.ewi.ceng.JniUtils;
import org.tudelft.ewi.ceng.annotations.JNI_class;
import org.tudelft.ewi.ceng.annotations.JNI_field;
import org.tudelft.ewi.ceng.annotations.JNI_method;
import org.tudelft.ewi.ceng.annotations.JNI_param;

import java.io.Serializable;

/**
 * Created by root on 7/21/16.
 */
@JNI_class public class RawArrBean implements Serializable{
    @JNI_field(alignment = JniUtils.MEM_ALIGNED_128) private double[] doubles;
    @JNI_field private double[] newArr;

    @JNI_method(target="constructor") public RawArrBean(@JNI_param(target="doubles") double[] doubles, @JNI_param(target="newArr") double[] newArr){
        this.doubles = doubles;
        this.newArr = newArr;
    }

    public double[] getArr(){
        return doubles;
    }

    public void setArr(double[] doubles){
        this.doubles = doubles;
    }
}
