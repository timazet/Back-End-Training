package com.timazet.aspect;

import java.lang.annotation.*;

/**
 * TODO added description
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    boolean attachExecutionDuration() default true;

}
