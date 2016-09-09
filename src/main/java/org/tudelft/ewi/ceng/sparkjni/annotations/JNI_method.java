package org.tudelft.ewi.ceng.sparkjni.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.CONSTRUCTOR)
public @interface JNI_method {
    public String target() default "default";
}