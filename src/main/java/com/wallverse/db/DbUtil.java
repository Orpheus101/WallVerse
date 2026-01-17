package com.wallverse.db;

import com.wallverse.config.DbConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    
    // This method helps us connect to the database
    public static Connection getConnection() throws SQLException {
        // Tell Java which database driver to use
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            // If it fails, we print an error
            System.out.println("Error: MySQL Driver not found!");
        }
        
        // Return the connection using the settings in DbConfig
        return DriverManager.getConnection(DbConfig.URL, DbConfig.USER, DbConfig.PASSWORD);
    }

    // This method checks if we can connect to the database right now
    public static boolean isAvailable() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null) {
                conn.close();
                return true;
            }
            return false;
        } catch (Exception ex) {
            // If any error happens, it means the database is not available
            return false;
        }
    }
}
