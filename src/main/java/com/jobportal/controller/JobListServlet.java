
package com.jobportal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.model.JobPage;
import com.jobportal.service.JobService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/jobs")
public class JobListServlet extends HttpServlet {

        private final JobService jobService = new JobService();
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        protected void doGet(
                        HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                try {
                        String keyword = request.getParameter("keyword");
                        String location = request.getParameter("location");
                        String jobType = request.getParameter("jobType");

                        int page = parseIntegerParameter(
                                        request.getParameter("page"), 1, "page");

                        int size = parseIntegerParameter(
                                        request.getParameter("size"), 10, "size");

                        JobPage result = jobService.searchJobs(
                                        keyword,
                                        location,
                                        jobType,
                                        page,
                                        size);

                        objectMapper.writeValue(
                                        response.getWriter(),
                                        result);

                } catch (IllegalArgumentException e) {
                        response.setStatus(
                                        HttpServletResponse.SC_BAD_REQUEST);

                        objectMapper.writeValue(
                                        response.getWriter(),
                                        Map.of("error", e.getMessage()));

                } catch (SQLException e) {
                        getServletContext().log(
                                        "Failed to retrieve job listings.", e);

                        response.setStatus(
                                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        objectMapper.writeValue(
                                        response.getWriter(),
                                        Map.of("error",
                                                        "Unable to retrieve job listings."));
                }
        }

        private int parseIntegerParameter(
                        String value,
                        int defaultValue,
                        String parameterName) {

                if (value == null || value.isBlank()) {
                        return defaultValue;
                }

                try {
                        return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                        parameterName + " must be a valid integer.");
                }
        }
}
