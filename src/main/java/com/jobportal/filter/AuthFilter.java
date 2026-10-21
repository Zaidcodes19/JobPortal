
package com.jobportal.filter;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({
        "/api/protected/*",
        "/api/recruiter/*",
        "/api/job-seeker/*"
})
public class AuthFilter implements Filter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || session.getAttribute("userRole") == null) {

            writeError(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authentication required.");
            return;
        }

        String path = request.getServletPath();
        String role = (String) session.getAttribute("userRole");

        if (path.startsWith("/api/recruiter/")
                && !"RECRUITER".equals(role)) {

            writeError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Recruiter access required.");
            return;
        }

        if (path.startsWith("/api/job-seeker/")
                && !"JOB_SEEKER".equals(role)) {

            writeError(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Job seeker access required.");
            return;
        }

        chain.doFilter(request, response);
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String message) throws IOException {

        response.setStatus(status);

        objectMapper.writeValue(
                response.getWriter(),
                Map.of("error", message));
    }
}
