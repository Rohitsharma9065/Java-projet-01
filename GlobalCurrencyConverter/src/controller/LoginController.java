package controller;

import model.User;
import service.UserService;

/**
 * LoginController.java
 * ---------------------
 * Handles Login and Registration requests.
 * Acts as the "C" in MVC pattern.
 *
 * In a real web app, this would handle HTTP requests.
 * Here it simulates controller logic for learning purposes.
 *
 * OOP Concepts Used:
 *   - Abstraction   : Controller calls service methods, not DAO directly
 *   - Encapsulation : Logic encapsulated in methods
 *
 * Author: Rohit Sharma
 */
public class LoginController {

    // Service layer — the controller delegates to service
    private UserService userService = new UserService();

    // -------------------------------------------------------
    // Handle Registration Request
    // -------------------------------------------------------
    public String register(String name, String email, String password, String confirmPassword) {

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            return "ERROR: Passwords do not match.";
        }

        // Delegate to service layer
        return userService.registerUser(name, email, password);
    }

    // -------------------------------------------------------
    // Handle Login Request
    // Returns User object on success, null on failure
    // -------------------------------------------------------
    public User login(String email, String password) {
        User user = userService.loginUser(email, password);

        if (user != null) {
            System.out.println("✅ Login successful for: " + user.getName() + " [" + user.getRole() + "]");
        } else {
            System.out.println("❌ Login failed for: " + email);
        }

        return user;
    }

    // -------------------------------------------------------
    // Handle Logout (clear session)
    // In a real web app, this would invalidate the session/token
    // -------------------------------------------------------
    public String logout() {
        // Session is managed in JavaScript (localStorage)
        System.out.println("👋 User logged out.");
        return "SUCCESS: Logged out successfully.";
    }
}
