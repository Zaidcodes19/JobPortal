
// package com.jobportal.controller;

// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.SQLException;

// import com.jobportal.util.DBConnection;

// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @WebServlet("/api/db-test")
// public class DBTestServlet extends HttpServlet {

// @Override
// protected void doGet(
// HttpServletRequest request,
// HttpServletResponse response) throws IOException {

// response.setContentType("text/plain");
// response.setCharacterEncoding("UTF-8");

// try (Connection connection = DBConnection.getConnection()) {
// response.setStatus(HttpServletResponse.SC_OK);
// response.getWriter().println(
// "Database connection successful!");

// } catch (SQLException | IllegalStateException e) {
// e.printStackTrace();
// getServletContext().log("Database connection failed", e);
// response.setStatus(
// HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
// response.getWriter().println(
// "Database connection failed. Check the server logs.");
// }
// }
// }
