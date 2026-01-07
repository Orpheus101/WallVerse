package com.wallverse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.wallverse.config.DatabaseConfig;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
            return DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB JDBC Driver not found", e);
        }
    }
}
