package dao;

import database.DBConnection;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO.java
 * -------------
 * Data Access Object for User-related database operations.
 * Handles all SQL queries for the 'users' table.
 *
 * OOP Concepts Used:
 *   - Abstraction   : Hides SQL complexity from service layer
 *   - Encapsulation : Business logic separated from DB operations
 *
 * Author: Rohit Sharma
 */
public class UserDAO {

    // -------------------------------------------------------
    // Register a new user in the database
    // -------------------------------------------------------
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            int rows = ps.executeUpdate();
            return rows > 0;  // true if insert was successful

        } catch (SQLException e) {
            System.out.println("❌ Error registering user: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Login — validate email and password
    // -------------------------------------------------------
    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Build a User object from the result
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));

                // Update last login time
                updateLastLogin(user.getId());
                return user;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error during login: " + e.getMessage());
        }
        return null;  // null means login failed
    }

    // -------------------------------------------------------
    // Check if an email already exists (for registration)
    // -------------------------------------------------------
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // true if a row was found

        } catch (SQLException e) {
            System.out.println("❌ Error checking email: " + e.getMessage());
        }
        return false;
    }

    // -------------------------------------------------------
    // Update the last login timestamp for a user
    // -------------------------------------------------------
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Error updating last login: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Get all users (Admin feature)
    // -------------------------------------------------------
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getString("last_login"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching users: " + e.getMessage());
        }
        return users;
    }

    // -------------------------------------------------------
    // Get a user by ID
    // -------------------------------------------------------
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getString("last_login"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching user: " + e.getMessage());
        }
        return null;
    }
}
