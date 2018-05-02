package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JdbcOraclePreparedStatementDogDao extends JdbcPreparedStatementDogDao {

    public JdbcOraclePreparedStatementDogDao(DataSource dataSource) {
        super(dataSource);
    }

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on oracle jdbc prepared statement usage");
    }

    @Override
    public Dog get(final UUID id) {
        return executeQuery("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = ?",
                statement -> statement.setString(1, Optional.ofNullable(id).map(UUID::toString).orElse(null)),
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
                    statement.setString(1, Optional.ofNullable(dog.getId()).map(UUID::toString).orElse(null));
                    statement.setString(2, dog.getName());
                    statement.setObject(3, Optional.ofNullable(dog.getBirthDate()).map(LocalDate::toEpochDay)
                            .map(days -> new Date(TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS))).orElse(null));
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
                    statement.setObject(2, Optional.ofNullable(dog.getBirthDate()).map(LocalDate::toEpochDay)
                            .map(days -> new Date(TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS))).orElse(null));
                    statement.setInt(3, dog.getHeight());
                    statement.setInt(4, dog.getWeight());
                    statement.setString(5, Optional.ofNullable(dog.getId()).map(UUID::toString).orElse(null));
                });
        if (count <= 0) {
            throw new DogNotFoundException(dog.getId());
        }
        return dog;
    }

    @Override
    public void delete(final UUID id) {
        int count = executeUpdate("DELETE FROM DOG where id = ?", statement ->
                statement.setString(1, Optional.ofNullable(id).map(UUID::toString).orElse(null)));
        if (count <= 0) {
            throw new DogNotFoundException(id);
        }
    }

    @Override
    protected Dog convertSingle(ResultSet resultSet) throws SQLException {
        Dog result = new Dog();
        result.setId(UUID.fromString(resultSet.getString(Dog.ID)));
        result.setName(resultSet.getString(Dog.NAME));
        result.setBirthDate(Optional.ofNullable(resultSet.getDate(Dog.BIRTH_DATE))
                .map(Date::toLocalDate).orElse(null));
        result.setHeight(resultSet.getInt(Dog.HEIGHT));
        result.setWeight(resultSet.getInt(Dog.WEIGHT));
        return result;
    }
}
