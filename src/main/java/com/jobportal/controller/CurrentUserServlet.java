
package com.jobportal.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/auth/me")
public class CurrentUserServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Don't create a new session just to check authentication.
        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || session.getAttribute("userRole") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", "You are not logged in."));
            return;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", session.getAttribute("userId"));
        result.put("role", session.getAttribute("userRole"));

        response.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(response.getWriter(), result);
    }
}
