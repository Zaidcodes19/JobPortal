
package com.jobportal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobNotFoundException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/job-seeker/applications")
public class ApplyJobServlet extends HttpServlet {

    private final ApplicationService applicationService = new ApplicationService();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class ApplyJobRequest {
        public Integer jobId;
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ApplyJobRequest data = objectMapper.readValue(
                    request.getInputStream(),
                    ApplyJobRequest.class);

            HttpSession session = request.getSession(false);

            // AuthFilter should already have authenticated the request.
            if (session == null
                    || !"JOB_SEEKER".equals(
                            session.getAttribute("userRole"))
                    || !(session.getAttribute("userId") instanceof Integer applicantId)) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("error", "Job seeker session required."));
                return;
            }

            if (data == null || data.jobId == null) {
                throw new IllegalArgumentException(
                        "Job ID is required.");
            }

            int applicationId = applicationService.applyForJob(
                    data.jobId,
                    applicantId);

            response.setStatus(
                    HttpServletResponse.SC_CREATED);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of(
                            "message", "Application submitted successfully.",
                            "applicationId", applicationId,
                            "jobId", data.jobId));

        } catch (JsonProcessingException e) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", "Invalid JSON request."));

        } catch (JobNotFoundException e) {
            response.setStatus(
                    HttpServletResponse.SC_NOT_FOUND);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            getServletContext().log(
                    "Job application submission failed.", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of(
                            "error",
                            "Unable to submit application. Please try again."));
        }
    }
}
