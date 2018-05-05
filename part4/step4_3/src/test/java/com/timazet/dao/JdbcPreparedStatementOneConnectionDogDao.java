package com.timazet.dao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class JdbcPreparedStatementOneConnectionDogDao extends JdbcPreparedStatementDogDao {

    @Getter(AccessLevel.PROTECTED)
    private volatile Connection connection;

    public JdbcPreparedStatementOneConnectionDogDao(DataSource dataSource) {
        super(dataSource);
    }

    protected void init() {
        log.info("Using DogDao based on jdbc prepared statement and only one opened connection usage");
        try {
            connection = getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred during initialization", e);
        }
    }

    @PreDestroy
    protected void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error occurred during destroying", e);
            }
        }
    }

    @Override
    protected <T> T executeQuery(final String query, final PrepareAction action, final Converter<T> converter) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return converter.convert(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int executeUpdate(final String query, final PrepareAction action) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            action.apply(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
