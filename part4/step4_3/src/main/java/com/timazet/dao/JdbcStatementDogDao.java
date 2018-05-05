package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JdbcStatementDogDao extends JdbcDogDao {

    @Getter(AccessLevel.PROTECTED)
    private final DataSource dataSource;

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on jdbc statement usage");
    }

    @Override
    public Collection<Dog> get() {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG", this::convert);
    }

    @Override
    public Dog get(final UUID id) {
        return executeQuery(String.format("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = %s", wrapIfNecessary(id)), resultSet -> {
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
        executeUpdate(String.format("INSERT INTO DOG (id, name, birth_date, height, weight) values (%s, %s, %s, %d, %d)",
                wrapIfNecessary(dog.getId()), wrapIfNecessary(dog.getName()), wrapIfNecessary(dog.getBirthDate()),
                dog.getHeight(), dog.getWeight()));
        return dog;
    }

    @Override
    public Dog update(final Dog dog) {
        int count = executeUpdate(String.format("UPDATE DOG SET name = %s, birth_date = %s, height = %d, weight = %d where id = %s",
                wrapIfNecessary(dog.getName()), wrapIfNecessary(dog.getBirthDate()), dog.getHeight(), dog.getWeight(),
                wrapIfNecessary(dog.getId())));
        if (count <= 0) {
            throw new DogNotFoundException(dog.getId());
        }
        return dog;
    }

    @Override
    public void delete(final UUID id) {
        int count = executeUpdate(String.format("DELETE FROM DOG where id = %s", wrapIfNecessary(id)));
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

    private static String wrapIfNecessary(Object parameter) {
        return Optional.ofNullable(parameter).map(value -> "'" + value.toString() + "'").orElse(null);
    }

}
