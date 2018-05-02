package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JdbcPreparedStatementDogDao extends JdbcDogDao implements DogDao {

    private final DataSource dataSource;

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on jdbc prepared statement usage");
    }

    @Override
    public Collection<Dog> get() {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG", statement -> {
        }, this::convert);
    }

    @Override
    public Dog get(final UUID id) {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = ?",
                statement -> statement.setObject(1, id),
                resultSet -> {
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
        executeUpdate("INSERT INTO DOG (id, name, birth_date, height, weight) values (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setObject(1, dog.getId());
                    statement.setString(2, dog.getName());
                    statement.setObject(3, dog.getBirthDate());
                    statement.setInt(4, dog.getHeight());
                    statement.setInt(5, dog.getWeight());
                });
        return dog;
    }

    @Override
    public Dog update(final Dog dog) {
        int count = executeUpdate("UPDATE DOG SET name = ?, birth_date = ?, height = ?, weight = ? where id = ?",
                statement -> {
                    statement.setString(1, dog.getName());
                    statement.setObject(2, dog.getBirthDate());
                    statement.setInt(3, dog.getHeight());
                    statement.setInt(4, dog.getWeight());
                    statement.setObject(5, dog.getId());
                });
        if (count <= 0) {
            throw new DogNotFoundException(dog.getId());
        }
        return dog;
    }

    @Override
    public void delete(final UUID id) {
        int count = executeUpdate("DELETE FROM DOG where id = ?", statement -> statement.setObject(1, id));
        if (count <= 0) {
            throw new DogNotFoundException(id);
        }
    }

    protected <T> T executeQuery(final String query, final PrepareAction action, final Converter<T> converter) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return converter.convert(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int executeUpdate(final String query, final PrepareAction action) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    protected interface PrepareAction {
        void apply(PreparedStatement statement) throws SQLException;
    }

}
