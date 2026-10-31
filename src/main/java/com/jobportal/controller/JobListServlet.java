
package com.jobportal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.model.Job;
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

                        List<Job> jobs = jobService.searchJobs(
                                        keyword,
                                        location,
                                        jobType);

                        objectMapper.writeValue(
                                        response.getWriter(),
                                        jobs);

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
}
