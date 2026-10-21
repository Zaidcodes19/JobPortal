
package com.jobportal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("D:/JobPortal")
            .ignoreIfMissing()
            .load();

    private static final String URL = dotenv.get("DB_URL", System.getenv("DB_URL"));

    private static final String USERNAME = dotenv.get("DB_USERNAME", System.getenv("DB_USERNAME"));

    private static final String PASSWORD = dotenv.get("DB_PASSWORD", System.getenv("DB_PASSWORD"));

    public static Connection getConnection() throws SQLException {

        if (URL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalStateException(
                    "Database configuration is missing. Check your .env file.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver is missing from the application.", e);
        }

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

}
