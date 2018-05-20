package com.timazet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an aspect in native 'aj' notation<br/>
 * It wraps some Statement implementations by H2 JDBC Driver
 */
public aspect StatementAspect {

    private static final Logger log = LoggerFactory.getLogger(StatementAspect.class);

    pointcut callAtExecute():
            // specify a cut for method in class in org.h2.jdbc package which ends by 'Statement'
            // name of method should start by 'execute' and method can contain any number of arguments
            // and return any type
            execution(* org.h2.jdbc.*Statement.execute*(..));

    before(): callAtExecute() {
        log.info("Before - '{}'", thisJoinPoint.toShortString());
    }

    after(): callAtExecute() {
        log.info("After - '{}'", thisJoinPoint.toShortString());
    }

    /*
     * There are 3 types of pointcut:
     *  - method pointcut: it can be represented by 'call', 'execution' and 'withincode' expressions
     *      e.g.: "[method designator](* com.timazet.*.*(..))"
     *          - 'call' will find all methods that calls a method in 'com.timazet' package
     *          - 'execute' will find all methods in 'com.timazet' package
     *          - 'withincode' will find all statements inside the methods in 'com.timazet' package
     *  - type pointcut: it can be represented only by 'within' expression
     *      e.g.: "[type designator](*..*Test)"
     *          - 'within' will find all statements inside classes that ends with 'Test'
     *  - field pointcut: it can be represented by 'get' and 'set' expressions
     *      e.g.: "[field designator](private org.springframework.jdbc.core.JdbcTemplate com.timazet..*.jdbcTemplate)"
     *          - 'get' will find all reads to 'jdbcTemplate' fields of type JdbcTemplate in 'com.timazet' package
     *          - 'set' will find all sets to 'jdbcTemplate' fields of type JdbcTemplate in 'com.timazet' package
     */

}