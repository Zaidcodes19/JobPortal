
package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jobportal.util.DBConnection;

public class ApplicationDAO {

    public boolean jobExists(int jobId) throws SQLException {

        String sql = "SELECT 1 FROM jobs WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, jobId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public int applyForJob(int jobId, int applicantId)
            throws SQLException {

        String sql = """
                INSERT INTO applications (
                    job_id,
                    applicant_id
                )
                VALUES (?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, jobId);
            statement.setInt(2, applicantId);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                throw new SQLException(
                        "The job application could not be submitted.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException(
                    "Application created, but its ID could not be retrieved.");
        }
    }
}
