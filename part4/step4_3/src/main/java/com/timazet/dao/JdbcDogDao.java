package com.timazet.dao;

import com.timazet.controller.dto.Dog;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class JdbcDogDao {

    protected List<Dog> convert(ResultSet resultSet) throws SQLException {
        List<Dog> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(convertSingle(resultSet));
        }
        return result;
    }

    protected Dog convertSingle(ResultSet resultSet) throws SQLException {
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
    interface Converter<T> {
        T convert(ResultSet resultSet) throws SQLException;
    }

}
