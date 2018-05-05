package com.timazet.dao;

import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
public class JdbcConnectionHolder {

    private final ThreadLocal<Connection> holder = new ThreadLocal<>();

    private final DataSource dataSource;

    public Connection getConnection() {
        Connection connection = holder.get();
        try {
            if (connection == null) {
                connection = dataSource.getConnection();
                holder.set(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void closeConnection() {
        Connection connection = holder.get();
        try {
            if (connection != null) {
                connection.close();
                holder.remove();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
