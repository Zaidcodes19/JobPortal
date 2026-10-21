
package com.jobportal.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/auth/register")
public class RegisterServlet extends HttpServlet {

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

            boolean registered = userService.registerUser(
                    data.get("name"),
                    data.get("email"),
                    data.get("password"),
                    data.get("role"));

            if (registered) {
                response.setStatus(
                        HttpServletResponse.SC_CREATED);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("message",
                                "Registration successful."));
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            objectMapper.writeValue(
                    response.getWriter(),
                    Map.of("error", e.getMessage()));

        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                response.setStatus(
                        HttpServletResponse.SC_CONFLICT);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("error",
                                "This email may already be registered."));
            } else {
                getServletContext().log(
                        "Registration database error", e);

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                objectMapper.writeValue(
                        response.getWriter(),
                        Map.of("error",
                                "Registration failed. Please try again."));
            }
        }
    }
}
