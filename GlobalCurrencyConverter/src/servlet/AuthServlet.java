package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.LoginController;
import model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthServlet.java
 * -----------------
 * Handles HTTP requests for authentication.
 *
 * Endpoints:
 *   POST /api/auth/login    → login user
 *   POST /api/auth/register → register new user
 *   GET  /api/auth/logout   → logout (clear session)
 *   GET  /api/auth/me       → get current logged-in user
 *
 * OOP Concepts: Inheritance (extends HttpServlet), Encapsulation
 * Author: Rohit Sharma
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    // Jackson converts Java objects ↔ JSON
    private ObjectMapper mapper = new ObjectMapper();

    // Business logic layer
    private LoginController loginController = new LoginController();

    // -------------------------------------------------------
    // Handle POST requests (login, register)
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // Tell the browser we're returning JSON
        setCorsHeaders(resp);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Get the last part of the URL (e.g. "/login" or "/register")
        String action = req.getPathInfo();   // e.g. "/login"

        Map<String, Object> result = new HashMap<>();

        try {
            if ("/login".equals(action)) {
                handleLogin(req, resp, result);

            } else if ("/register".equals(action)) {
                handleRegister(req, resp, result);

            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "Unknown action: " + action);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
        }

        // Write JSON response
        mapper.writeValue(resp.getOutputStream(), result);
    }

    // -------------------------------------------------------
    // Handle GET requests (logout, me)
    // -------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String action = req.getPathInfo();
        Map<String, Object> result = new HashMap<>();

        if ("/logout".equals(action)) {
            // Invalidate the server-side session
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();

            result.put("success", true);
            result.put("message", "Logged out successfully.");

        } else if ("/me".equals(action)) {
            // Return the currently logged-in user
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                result.put("success", true);
                result.put("user", session.getAttribute("user"));
            } else {
                result.put("success", false);
                result.put("message", "Not logged in.");
            }
        }

        mapper.writeValue(resp.getOutputStream(), result);
    }

    // -------------------------------------------------------
    // Handle preflight CORS OPTIONS request
    // -------------------------------------------------------
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // -------------------------------------------------------
    // LOGIN logic
    // -------------------------------------------------------
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp,
                             Map<String, Object> result) throws IOException {

        // Read JSON body from request
        @SuppressWarnings("unchecked")
        Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);

        String email    = body.getOrDefault("email",    "");
        String password = body.getOrDefault("password", "");

        User user = loginController.login(email, password);

        if (user != null) {
            // Save user to session
            HttpSession session = req.getSession(true);
            session.setAttribute("user",   user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role",   user.getRole());

            // Build safe response (don't send password)
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id",        user.getId());
            userMap.put("name",      user.getName());
            userMap.put("email",     user.getEmail());
            userMap.put("role",      user.getRole());
            userMap.put("lastLogin", user.getLastLogin());

            result.put("success", true);
            result.put("message", "Login successful! Welcome, " + user.getName());
            result.put("user",    userMap);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "Invalid email or password. Please try again.");
        }
    }

    // -------------------------------------------------------
    // REGISTER logic
    // -------------------------------------------------------
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp,
                                Map<String, Object> result) throws IOException {

        @SuppressWarnings("unchecked")
        Map<String, String> body = mapper.readValue(req.getInputStream(), Map.class);

        String name            = body.getOrDefault("name",            "");
        String email           = body.getOrDefault("email",           "");
        String password        = body.getOrDefault("password",        "");
        String confirmPassword = body.getOrDefault("confirmPassword", "");

        String msg = loginController.register(name, email, password, confirmPassword);

        boolean success = msg.startsWith("SUCCESS");
        if (!success) resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        result.put("success", success);
        // Strip the "SUCCESS: " or "ERROR: " prefix for cleaner message
        result.put("message", msg.contains(": ") ? msg.split(": ", 2)[1] : msg);
    }

    // -------------------------------------------------------
    // Set CORS headers so the frontend can call this API
    // -------------------------------------------------------
    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin",  "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
