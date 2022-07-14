package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSourceInitializer {

    public static Connection getConnection() {
        try {
            // allow multiple queries myst be used in the stored procedure is called set max concat length
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/db?allowMultiQueries=true", "root", "password");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
