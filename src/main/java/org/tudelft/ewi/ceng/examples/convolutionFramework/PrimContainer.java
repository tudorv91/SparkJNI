package org.tudelft.ewi.ceng.examples.convolutionFramework;

import java.io.Serializable;

/**
 *
 */
public class PrimContainer implements Serializable{
    int[] arr;
    boolean FPGA_directed = true;

    /**
     * Just reference it, don't copy.
     * @param inArr input primitive array.
     *
     */

    public PrimContainer(int[] inArr, boolean fpga){
        arr = inArr;
        FPGA_directed = fpga;
    }

    protected PrimContainer(){};

    public int[] getRawArr(){
        return arr;
    }

    public boolean getHardwareTarget(){
        return FPGA_directed;
    }
}
