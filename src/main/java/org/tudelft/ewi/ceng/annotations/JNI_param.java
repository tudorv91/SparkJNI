package org.tudelft.ewi.ceng.annotations;

import org.tudelft.ewi.ceng.JniUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by root on 7/19/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.PARAMETER)
public @interface JNI_param {
    String target() default JniUtils.CONSTRUCTOR_PARAM_DEFAULT_NAME_MAPPING;
}