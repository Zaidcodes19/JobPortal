
package com.jobportal.service;

import java.util.List;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.jobportal.dao.JobDAO;
import com.jobportal.model.Job;

public class JobService {

    private final JobDAO jobDAO = new JobDAO();

    public Job createJob(Job job, int recruiterId)
            throws SQLException {

        if (job == null) {
            throw new IllegalArgumentException(
                    "Job details are required.");
        }

        validateRequiredField(job.getTitle(), "Job title");
        validateRequiredField(job.getCompanyName(), "Company name");
        validateRequiredField(job.getLocation(), "Location");
        validateRequiredField(job.getJobType(), "Job type");
        validateRequiredField(job.getDescription(), "Description");

        if (recruiterId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid recruiter.");
        }

        BigDecimal min = job.getSalaryMin();
        BigDecimal max = job.getSalaryMax();

        if (min != null && min.signum() < 0) {
            throw new IllegalArgumentException(
                    "Minimum salary cannot be negative.");
        }

        if (max != null && max.signum() < 0) {
            throw new IllegalArgumentException(
                    "Maximum salary cannot be negative.");
        }

        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException(
                    "Minimum salary cannot exceed maximum salary.");
        }

        // Always use the authenticated recruiter's ID.
        job.setRecruiterId(recruiterId);

        boolean created = jobDAO.createJob(job);

        if (!created) {
            throw new SQLException(
                    "The job could not be created.");
        }

        return job;
    }

    public List<Job> getAllJobs() throws SQLException {
        return jobDAO.getAllJobs();
    }

    private void validateRequiredField(
            String value,
            String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " is required.");
        }
    }
}
