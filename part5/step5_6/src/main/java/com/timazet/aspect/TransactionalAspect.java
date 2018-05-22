package com.timazet.aspect;

import com.timazet.dao.JdbcConnectionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class TransactionalAspect {

    private final JdbcConnectionHolder connectionHolder;

    @Pointcut("@annotation(MyTransactional)")
    public void executeWithTransaction() {
    }

    @Around("executeWithTransaction()")
    public Object invoke(ProceedingJoinPoint jp) throws Throwable {
        log.info("Processing joint point '{}'", jp.toShortString());
        try {
            Connection connection = connectionHolder.getConnection();
            connection.setAutoCommit(false);
            try {
                Object result = jp.proceed(jp.getArgs());
                connection.commit();
                return result;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionHolder.closeConnection();
        }
    }

}
