package org.tudelft.ceng.sparkjni.exceptions;

/**
 * Created by root on 9/24/16.
 */
public class SoftSparkJniException extends Exception {
    public SoftSparkJniException(String message) {
        super("[Exception] " + message);
    }
}
