package service;

import dao.UserDAO;
import model.User;

import java.util.List;

/**
 * UserService.java
 * -----------------
 * Business logic layer for User management.
 * Validates data before passing it to UserDAO.
 *
 * OOP Concepts Used:
 *   - Encapsulation  : Validation logic hidden in methods
 *   - Abstraction    : Controllers just call service methods
 *
 * Author: Rohit Sharma
 */
public class UserService {

    // DAO object to access the database
    private UserDAO userDAO = new UserDAO();

    // -------------------------------------------------------
    // Register a new user
    // Returns a message string so the controller can show it
    // -------------------------------------------------------
    public String registerUser(String name, String email, String password) {

        // Validation 1: Check for empty fields
        if (name == null || name.trim().isEmpty()) {
            return "ERROR: Name cannot be empty.";
        }
        if (email == null || email.trim().isEmpty()) {
            return "ERROR: Email cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "ERROR: Password cannot be empty.";
        }

        // Validation 2: Basic email format check
        if (!email.contains("@") || !email.contains(".")) {
            return "ERROR: Invalid email format.";
        }

        // Validation 3: Password length
        if (password.length() < 6) {
            return "ERROR: Password must be at least 6 characters.";
        }

        // Validation 4: Check if email already registered
        if (userDAO.emailExists(email)) {
            return "ERROR: Email already registered. Please login.";
        }

        // All validations passed — create and save the user
        User user = new User(name.trim(), email.trim().toLowerCase(), password, "user");
        boolean success = userDAO.registerUser(user);

        return success ? "SUCCESS: Registration successful! Please login."
                       : "ERROR: Registration failed. Please try again.";
    }

    // -------------------------------------------------------
    // Login a user
    // Returns User object if successful, null otherwise
    // -------------------------------------------------------
    public User loginUser(String email, String password) {
        if (email == null || password == null) return null;
        return userDAO.loginUser(email.trim().toLowerCase(), password);
    }

    // -------------------------------------------------------
    // Get all users (Admin feature)
    // -------------------------------------------------------
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // -------------------------------------------------------
    // Get user by ID
    // -------------------------------------------------------
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    // -------------------------------------------------------
    // Check if a user is an admin
    // -------------------------------------------------------
    public boolean isAdmin(User user) {
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }
}
