
package com.jobportal.service;

import java.util.List;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.jobportal.dao.JobDAO;
import com.jobportal.model.Job;
import com.jobportal.model.JobPage;

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

    public JobPage searchJobs(
            String keyword,
            String location,
            String jobType,
            int page,
            int size) throws SQLException {

        if (page < 1) {
            throw new IllegalArgumentException(
                    "Page number must be at least 1.");
        }

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 50.");
        }

        long offsetLong = ((long) page - 1) * size;

        if (offsetLong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Page number is too large.");
        }

        int offset = (int) offsetLong;

        List<Job> jobs = jobDAO.searchJobs(
                keyword, location, jobType, size, offset);

        long totalJobs = jobDAO.countJobs(
                keyword, location, jobType);

        long totalPages = (totalJobs + size - 1) / size;

        return new JobPage(
                jobs, page, size, totalJobs, totalPages);
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
