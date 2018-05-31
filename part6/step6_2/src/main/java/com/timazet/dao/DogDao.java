package com.timazet.dao;

import com.timazet.controller.DogNotFoundException;
import com.timazet.controller.dto.Dog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DogDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Dog> rowMapper = new DogRowMapper();

    @PostConstruct
    private void init() {
        log.info("Using DogDao based on jdbc prepared statement usage");
    }

    public Collection<Dog> get() {
        return jdbcTemplate.query("SELECT id, name, birth_date, height, weight FROM DOG", (PreparedStatementSetter) null, rowMapper);
    }

    public Dog get(final UUID id) {
        List<Dog> result = jdbcTemplate.query("SELECT id, name, birth_date, height, weight FROM DOG WHERE id = ?",
                statement -> statement.setObject(1, id), rowMapper);
        if (result.isEmpty()) {
            throw new DogNotFoundException(id);
        }
        return result.get(0);
    }

    public Dog create(final Dog dog) {
        dog.setId(UUID.randomUUID());
        jdbcTemplate.update("INSERT INTO DOG (id, name, birth_date, height, weight) values (?, ?, ?, ?, ?)",
                statement -> {
                    statement.setObject(1, dog.getId());
                    statement.setString(2, dog.getName());
                    statement.setObject(3, dog.getBirthDate());
                    statement.setInt(4, dog.getHeight());
                    statement.setInt(5, dog.getWeight());
                });
        return dog;
    }

    public Dog update(final Dog dog) {
        int count = jdbcTemplate.update("UPDATE DOG SET name = ?, birth_date = ?, height = ?, weight = ? where id = ?",
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

    public void delete(final UUID id) {
        int count = jdbcTemplate.update("DELETE FROM DOG where id = ?", statement -> statement.setObject(1, id));
        if (count <= 0) {
            throw new DogNotFoundException(id);
        }

    }

    private static class DogRowMapper implements RowMapper<Dog> {

        @Override
        public Dog mapRow(ResultSet rs, int rowNum) throws SQLException {
            Dog result = new Dog();
            result.setId((UUID) rs.getObject(Dog.ID));
            result.setName(rs.getString(Dog.NAME));
            result.setBirthDate(Optional.ofNullable(rs.getDate(Dog.BIRTH_DATE))
                    .map(Date::toLocalDate).orElse(null));
            result.setHeight(rs.getInt(Dog.HEIGHT));
            result.setWeight(rs.getInt(Dog.WEIGHT));
            return result;
        }

    }

}
