package com.timazet.dao;

import com.timazet.controller.dto.Dog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class DogDao {

    private final JdbcConnectionHolder connectionHolder;

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on jdbc prepared statement usage");
    }

    public Collection<Dog> get() {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG", statement -> {
        }, this::convert);
    }

    public Dog get(final UUID id) {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = ?",
                statement -> statement.setObject(1, id),
                resultSet -> {
                    List<Dog> result = convert(resultSet);
                    return result.isEmpty() ? null : result.get(0);
                });
    }

    public int create(final Dog dog) {
        return executeUpdate("INSERT INTO DOG (id, name, birth_date, height, weight) values (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setObject(1, dog.getId());
                    statement.setString(2, dog.getName());
                    statement.setObject(3, dog.getBirthDate());
                    statement.setInt(4, dog.getHeight());
                    statement.setInt(5, dog.getWeight());
                });
    }

    public int update(final Dog dog) {
        return executeUpdate("UPDATE DOG SET name = ?, birth_date = ?, height = ?, weight = ? where id = ?",
                statement -> {
                    statement.setString(1, dog.getName());
                    statement.setObject(2, dog.getBirthDate());
                    statement.setInt(3, dog.getHeight());
                    statement.setInt(4, dog.getWeight());
                    statement.setObject(5, dog.getId());
                });
    }

    public int delete(final UUID id) {
        return executeUpdate("DELETE FROM DOG where id = ?", statement -> statement.setObject(1, id));

    }

    private <T> T executeQuery(final String query, final PrepareAction action, final Converter<T> converter) {
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return converter.convert(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int executeUpdate(final String query, final PrepareAction action) {
        Connection connection = connectionHolder.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Dog> convert(ResultSet resultSet) throws SQLException {
        List<Dog> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(convertSingle(resultSet));
        }
        return result;
    }

    private Dog convertSingle(ResultSet resultSet) throws SQLException {
        Dog result = new Dog();
        result.setId((UUID) resultSet.getObject(Dog.ID));
        result.setName(resultSet.getString(Dog.NAME));
        result.setBirthDate(Optional.ofNullable(resultSet.getDate(Dog.BIRTH_DATE))
                .map(Date::toLocalDate).orElse(null));
        result.setHeight(resultSet.getInt(Dog.HEIGHT));
        result.setWeight(resultSet.getInt(Dog.WEIGHT));
        return result;
    }

    @FunctionalInterface
    private interface PrepareAction {
        void apply(PreparedStatement statement) throws SQLException;
    }

    @FunctionalInterface
    private interface Converter<T> {
        T convert(ResultSet resultSet) throws SQLException;
    }

}
