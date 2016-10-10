package org.heterojni.sparkjni.utils.exceptions;

/**
 * Created by root on 9/24/16.
 */
public class HardSparkJniException extends RuntimeException {
    public HardSparkJniException(String message) {
        super("[UNRECOVERABLE ERROR] " + message);
    }
}
