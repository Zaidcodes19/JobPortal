
package com.jobportal.dao;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public List<Job> getAllJobs() throws SQLException {
        return searchJobs(null, null, null, Integer.MAX_VALUE, 0);
    }

    public List<Job> searchJobs(
            String keyword,
            String location,
            String jobType,
            int limit,
            int offset) throws SQLException {

        List<Object> parameters = new ArrayList<>();

        StringBuilder sql = buildJobSearchQuery(
                keyword, location, jobType, parameters, false);

        sql.append(" ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?");

        parameters.add(limit);
        parameters.add(offset);

        List<Job> jobs = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
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
        }

        return jobs;
    }

    private StringBuilder buildJobSearchQuery(
            String keyword,
            String location,
            String jobType,
            List<Object> parameters,
            boolean countOnly) {

        StringBuilder sql;

        if (countOnly) {
            sql = new StringBuilder(
                    "SELECT COUNT(*) FROM jobs WHERE 1 = 1");
        } else {
            sql = new StringBuilder("""
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
                    WHERE 1 = 1
                    """);
        }

        if (keyword != null && !keyword.isBlank()) {
            sql.append("""
                     AND (
                        title LIKE ?
                        OR company_name LIKE ?
                     )
                    """);

            String pattern = "%" + keyword.trim() + "%";
            parameters.add(pattern);
            parameters.add(pattern);
        }

        if (location != null && !location.isBlank()) {
            sql.append(" AND location LIKE ?");
            parameters.add("%" + location.trim() + "%");
        }

        if (jobType != null && !jobType.isBlank()) {
            sql.append(" AND job_type = ?");
            parameters.add(jobType.trim().toUpperCase());
        }

        return sql;
    }

    public long countJobs(
            String keyword,
            String location,
            String jobType) throws SQLException {

        List<Object> parameters = new ArrayList<>();

        StringBuilder sql = buildJobSearchQuery(
                keyword, location, jobType, parameters, true);

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        }

        return 0;
    }
}
