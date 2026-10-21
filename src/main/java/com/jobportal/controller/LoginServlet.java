
package com.jobportal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.model.User;
import com.jobportal.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/auth/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, String> data = objectMapper.readValue(
                    request.getInputStream(),
                    objectMapper.getTypeFactory()
                            .constructMapType(
                                    Map.class,
                                    String.class,
                                    String.class));

            User user = userService.loginUser(
                    data.get("email"),
                    data.get("password"));

            if (user == null) {
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("error",
                                "Invalid email or password."));
                return;
            }

            // Prevent session fixation.
            HttpSession previousSession = request.getSession(false);

            if (previousSession != null) {
                previousSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Login successful.");
            result.put("userId", user.getId());
            result.put("name", user.getName());
            result.put("email", user.getEmail());
            result.put("role", user.getRole());

            response.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(response.getWriter(), result);

        } catch (IllegalArgumentException e) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            getServletContext().log("Login database error", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error",
                            "Login failed. Please try again."));
        }
    }
}
