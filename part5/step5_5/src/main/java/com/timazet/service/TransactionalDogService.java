package com.timazet.service;

import com.timazet.controller.dto.Dog;
import com.timazet.dao.JdbcConnectionHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

public class TransactionalDogService implements DogService {

    private final DogService delegate;
    private final JdbcConnectionHolder connectionHolder;

    public TransactionalDogService(final DogService delegate, final JdbcConnectionHolder connectionHolder) {
        this.delegate = delegate;
        this.connectionHolder = connectionHolder;
    }

    @Override
    public Collection<Dog> get() {
        return execute(delegate::get);
    }

    @Override
    public Dog get(UUID id) {
        return execute(() -> delegate.get(id));
    }

    @Override
    public Dog create(Dog dog) {
        return execute(() -> delegate.create(dog));
    }

    @Override
    public Dog update(Dog dog) {
        return execute(() -> delegate.update(dog));
    }

    @Override
    public void delete(UUID id) {
        execute(() -> {
            delegate.delete(id);
            return null;
        });
    }

    private <T> T execute(final Supplier<T> supplier) {
        try {
            Connection connection = connectionHolder.getConnection();
            connection.setAutoCommit(false);
            try {
                T result = supplier.get();
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
