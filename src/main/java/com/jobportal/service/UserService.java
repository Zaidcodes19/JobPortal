
package com.jobportal.service;

import java.sql.SQLException;
import java.util.Locale;

import org.mindrot.jbcrypt.BCrypt;

import com.jobportal.dao.UserDAO;
import com.jobportal.model.User;

public class UserService {

        private final UserDAO userDAO = new UserDAO();

        public boolean registerUser(
                        String name,
                        String email,
                        String password,
                        String role) throws SQLException {

                // Validate the input
                if (name == null || name.isBlank()
                                || email == null || email.isBlank()
                                || password == null || password.isBlank()
                                || role == null || role.isBlank()) {
                        throw new IllegalArgumentException(
                                        "All fields are required.");
                }

                name = name.trim();
                email = email.trim().toLowerCase(Locale.ROOT);
                role = role.trim().toUpperCase(Locale.ROOT);

                if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                        throw new IllegalArgumentException(
                                        "Please enter a valid email address.");
                }

                if (password.length() < 8) {
                        throw new IllegalArgumentException(
                                        "Password must be at least 8 characters.");
                }

                // Public registration must never create administrators.
                if (!role.equals("JOB_SEEKER")
                                && !role.equals("RECRUITER")) {
                        throw new IllegalArgumentException(
                                        "Invalid user role.");
                }

                // Hash the password before storing it.
                String hashedPassword = BCrypt.hashpw(
                                password,
                                BCrypt.gensalt(12));

                User user = new User(
                                name,
                                email,
                                hashedPassword,
                                role);

                return userDAO.registerUser(user);
        }

        public User loginUser(String email, String password)
                        throws SQLException {

                if (email == null || email.isBlank()
                                || password == null || password.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "Email and password are required.");
                }

                email = email.trim().toLowerCase(Locale.ROOT);

                User user = userDAO.findByEmail(email);

                if (user == null) {
                        return null;
                }

                boolean passwordMatches = BCrypt.checkpw(
                                password,
                                user.getPassword());

                if (!passwordMatches) {
                        return null;
                }

                return user;
        }

}
