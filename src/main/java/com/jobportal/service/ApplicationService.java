
package com.jobportal.service;

import java.sql.SQLException;

import com.jobportal.dao.ApplicationDAO;

public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public int applyForJob(int jobId, int applicantId)
            throws SQLException {

        if (jobId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid job ID.");
        }

        if (applicantId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid applicant.");
        }

        if (!applicationDAO.jobExists(jobId)) {
            throw new JobNotFoundException(
                    "Job not found.");
        }

        try {
            return applicationDAO.applyForJob(
                    jobId, applicantId);

        } catch (SQLException e) {

            // MySQL error code 1062: Duplicate entry.
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "You have already applied for this job.");
            }

            throw e;
        }
    }
}
