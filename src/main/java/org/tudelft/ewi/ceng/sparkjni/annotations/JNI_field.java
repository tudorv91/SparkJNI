package org.tudelft.ewi.ceng.sparkjni.annotations;

import org.tudelft.ewi.ceng.sparkjni.utils.JniUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by root on 7/19/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface JNI_field {
    String alignment() default JniUtils.MEM_UNALIGNED;
    String nativeTypeTarget() default JniUtils.NATIVE_TARGET_TYPE_JAVA_DEFINED;
    boolean safe() default true;
}