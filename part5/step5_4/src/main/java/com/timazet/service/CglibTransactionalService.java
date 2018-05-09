package com.timazet.service;

import com.timazet.dao.JdbcConnectionHolder;
import lombok.RequiredArgsConstructor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class CglibTransactionalService<T> implements InvocationHandler {

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
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(invocationTarget.getClass());
        enhancer.setCallback(new CglibTransactionalService<>(invocationTarget, connectionHolder));
        return (T) enhancer.create();
    }

}
