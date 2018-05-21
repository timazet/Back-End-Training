package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an aspect in native 'aj' notation<br/>
 * It wraps statements inside DogServiceImpl
 */
public aspect DogServiceAspect {

    private static final Logger log = LoggerFactory.getLogger(DogServiceAspect.class);

    pointcut withinDogService():
            within(com.timazet.service.DogServiceImpl);

    Object around(): withinDogService() {
        log.info("Before - '{}'", thisJoinPoint.toShortString());
        try {
            return proceed();
        } finally {
            log.info("After - '{}'", thisJoinPoint.toShortString());
        }
    }

}