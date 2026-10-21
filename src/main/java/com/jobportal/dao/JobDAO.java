
package com.jobportal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.jobportal.model.Job;
import com.jobportal.util.DBConnection;

public class JobDAO {

    public boolean createJob(Job job) throws SQLException {

        String sql = """
                INSERT INTO jobs (
                    recruiter_id,
                    title,
                    company_name,
                    location,
                    job_type,
                    description,
                    requirements,
                    salary_min,
                    salary_max
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, job.getRecruiterId());
            statement.setString(2, job.getTitle());
            statement.setString(3, job.getCompanyName());
            statement.setString(4, job.getLocation());
            statement.setString(5, job.getJobType());
            statement.setString(6, job.getDescription());
            statement.setString(7, job.getRequirements());
            statement.setBigDecimal(8, job.getSalaryMin());
            statement.setBigDecimal(9, job.getSalaryMax());

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted != 1) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    job.setId(keys.getInt(1));
                    return true;
                }
            }

            return false;
        }
    }

    public java.util.List<Job> getAllJobs() throws SQLException {

        String sql = """
                SELECT
                    id,
                    recruiter_id,
                    title,
                    company_name,
                    location,
                    job_type,
                    description,
                    requirements,
                    salary_min,
                    salary_max,
                    created_at
                FROM jobs
                ORDER BY created_at DESC, id DESC
                """;

        java.util.List<Job> jobs = new java.util.ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Job job = new Job();

                job.setId(resultSet.getInt("id"));
                job.setRecruiterId(resultSet.getInt("recruiter_id"));
                job.setTitle(resultSet.getString("title"));
                job.setCompanyName(resultSet.getString("company_name"));
                job.setLocation(resultSet.getString("location"));
                job.setJobType(resultSet.getString("job_type"));
                job.setDescription(resultSet.getString("description"));
                job.setRequirements(resultSet.getString("requirements"));
                job.setSalaryMin(resultSet.getBigDecimal("salary_min"));
                job.setSalaryMax(resultSet.getBigDecimal("salary_max"));
                job.setCreatedAt(resultSet.getTimestamp("created_at"));

                jobs.add(job);
            }
        }

        return jobs;
    }

}
