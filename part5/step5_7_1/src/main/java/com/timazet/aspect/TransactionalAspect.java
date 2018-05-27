package com.timazet.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class TransactionalAspect {

    private final PlatformTransactionManager txManager;

    @Pointcut("@annotation(MyTransactional)")
    public void executeWithTransaction() {
    }

    @Around("executeWithTransaction()")
    public Object invoke(ProceedingJoinPoint jp) throws Throwable {
        log.info("Processing joint point '{}'", jp.toShortString());
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(definition);
        Object result;
        try {
            result = jp.proceed(jp.getArgs());
        } catch (SQLException e) {
            txManager.rollback(status);
            throw new RuntimeException(e);
        } catch (Throwable throwable) {
            txManager.rollback(status);
            throw throwable;
        }
        txManager.commit(status);
        return result;

    }

}
