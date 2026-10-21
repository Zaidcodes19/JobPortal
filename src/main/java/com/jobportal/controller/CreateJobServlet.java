
package com.jobportal.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.model.Job;
import com.jobportal.service.JobService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/recruiter/jobs")
public class CreateJobServlet extends HttpServlet {

    private final JobService jobService = new JobService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class CreateJobRequest {
        public String title;
        public String companyName;
        public String location;
        public String jobType;
        public String description;
        public String requirements;
        public BigDecimal salaryMin;
        public BigDecimal salaryMax;
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            CreateJobRequest data = objectMapper.readValue(
                    request.getInputStream(),
                    CreateJobRequest.class);

            Job job = new Job();
            job.setTitle(data.title);
            job.setCompanyName(data.companyName);
            job.setLocation(data.location);
            job.setJobType(data.jobType);
            job.setDescription(data.description);
            job.setRequirements(data.requirements);
            job.setSalaryMin(data.salaryMin);
            job.setSalaryMax(data.salaryMax);

            HttpSession session = request.getSession(false);

            // AuthFilter should already have authenticated the request.
            if (session == null
                    || !"RECRUITER".equals(
                            session.getAttribute("userRole"))
                    || !(session.getAttribute("userId") instanceof Integer recruiterId)) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("error", "Recruiter session required."));
                return;
            }

            Job createdJob = jobService.createJob(job, recruiterId);

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), Map.of(
                    "message", "Job created successfully.",
                    "jobId", createdJob.getId(),
                    "title", createdJob.getTitle()));

        } catch (IllegalArgumentException e) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            getServletContext().log("Job creation failed", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error",
                            "Unable to create job. Please try again."));
        }
    }
}
