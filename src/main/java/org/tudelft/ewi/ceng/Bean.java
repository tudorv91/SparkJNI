package org.tudelft.ewi.ceng;

import java.io.Serializable;

/**
 * Created by root on 8/22/16.
 * Abstract to be inherited for all user-defined containers.
 */
public abstract class Bean implements Serializable {
    private static final int BILLION = 1000000000;
    long startRun;
    long endRun;
    long totalTime;

    public Bean(){}

    public long getTotalTimeNanos() {
        totalTime = endRun - startRun;
        return totalTime;
    }

    public double getTotalTimeSeconds() {
        double seconds = ((double)getTotalTimeNanos())/ BILLION;
        return seconds;
    }

    public long getStartRun() {
        return startRun;
    }

    public void setStartRun(long startRun) {
        this.startRun = startRun;
    }

    public long getEndRun() {
        return endRun;
    }

    public void setEndRun(long endRun) {
        this.endRun = endRun;
    }
}
