package com.timazet.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * This is an aspect in 'java-annotation' notation<br/>
 * It logs method calls annotated with {@link Log}
 */
@Aspect
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(logAnnotation)")
    public void callAt(Log logAnnotation) {
    }

    @Around(value = "callAt(logAnnotation)")
    public Object around(ProceedingJoinPoint jp, Log logAnnotation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        if (logAnnotation.attachExecutionDuration()) {
            stopWatch.start(jp.toShortString());
        }

        try {
            Object result = jp.proceed(jp.getArgs());
            log.info("Method '{}' is executed", jp.toShortString());
            return result;
        } catch (Throwable throwable) {
            log.info("Method '{}' is failed", jp.toShortString());
            throw throwable;
        } finally {
            if (logAnnotation.attachExecutionDuration()) {
                stopWatch.stop();
                log.info("{}", stopWatch.prettyPrint());
            }
        }
    }

}
