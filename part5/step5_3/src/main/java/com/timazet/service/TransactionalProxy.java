package com.timazet.service;

import com.timazet.dao.JdbcConnectionHolder;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class TransactionalProxy<T> implements InvocationHandler {

    private final T invocationTarget;
    private final JdbcConnectionHolder connectionHolder;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Connection connection = connectionHolder.getConnection();
            connection.setAutoCommit(false);
            try {
                Object result = method.invoke(invocationTarget, args);
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

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(final T invocationTarget, final JdbcConnectionHolder connectionHolder) {
        return (T) Proxy.newProxyInstance(invocationTarget.getClass().getClassLoader(), invocationTarget.getClass().getInterfaces(),
                new TransactionalProxy(invocationTarget, connectionHolder));
    }

}
