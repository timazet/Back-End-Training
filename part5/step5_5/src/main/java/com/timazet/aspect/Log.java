package com.timazet.aspect;

import java.lang.annotation.*;

/**
 * Annotation that is used to find out what methods should be logged
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    boolean attachExecutionDuration() default true;

}
