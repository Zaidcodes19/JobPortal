
package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.jobportal.model.User;
import com.jobportal.util.DBConnection;

public class UserDAO {

    public boolean registerUser(User user) throws SQLException {

        String sql = """
                INSERT INTO users (name, email, password, role)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());

            return statement.executeUpdate() == 1;
        }
    }

    // Add this method inside your existing UserDAO class.

    public User findByEmail(String email) throws SQLException {

        String sql = """
                SELECT id, name, email, password, role
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    User user = new User();

                    user.setId(resultSet.getInt("id"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));

                    return user;
                }

                return null;
            }
        }
    }

}
