package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import lombok.AllArgsConstructor;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@AllArgsConstructor
public class JdbcDogDao implements DogDao {

    private DataSource dataSource;

    @Override
    public Collection<Dog> get() {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG", JdbcDogDao::convert);
    }

    @Override
    public Dog get(final UUID id) {
        return executeQuery(String.format("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = '%s'", id), resultSet -> {
            List<Dog> result = convert(resultSet);
            if (result.isEmpty()) {
                throw new DogNotFoundException(id);
            }
            return result.get(0);
        });
    }

    @Override
    public Dog create(final Dog dog) {
        dog.setId(UUID.randomUUID());
        executeUpdate(String.format("INSERT INTO DOG (id, name, birth_date, height, weight) values ('%s', '%s', '%s', %d, %d)",
                dog.getId(), dog.getName(), dog.getBirthDate(), dog.getHeight(), dog.getWeight()));
        return dog;
    }

    @Override
    public Dog update(final Dog dog) {
        int count = executeUpdate(String.format("UPDATE DOG SET name = '%s', birth_date = '%s', height = %d, weight = %d where id = '%s'",
                dog.getName(), dog.getBirthDate(), dog.getHeight(), dog.getWeight(), dog.getId()));
        if (count <= 0) {
            throw new DogNotFoundException(dog.getId());
        }
        return dog;
    }

    @Override
    public void delete(final UUID id) {
        int count = executeUpdate(String.format("DELETE FROM DOG where id = '%s'", id));
        if (count <= 0) {
            throw new DogNotFoundException(id);
        }
    }

    private <T> T executeQuery(final String query, final Converter<T> converter) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            return converter.convert(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int executeUpdate(final String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Dog> convert(ResultSet resultSet) throws SQLException {
        List<Dog> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(convertSingle(resultSet));
        }
        return result;
    }

    private static Dog convertSingle(ResultSet resultSet) throws SQLException {
        Dog result = new Dog();
        result.setId((UUID) resultSet.getObject(Dog.ID));
        result.setName(resultSet.getString(Dog.NAME));
        result.setBirthDate(Optional.of(resultSet.getDate(Dog.BIRTH_DATE))
                .map(Date::toLocalDate).orElse(null));
        result.setHeight(resultSet.getInt(Dog.HEIGHT));
        result.setWeight(resultSet.getInt(Dog.WEIGHT));
        return result;
    }


    @FunctionalInterface
    interface Converter<T> {
        T convert(ResultSet resultSet) throws SQLException;
    }

}
