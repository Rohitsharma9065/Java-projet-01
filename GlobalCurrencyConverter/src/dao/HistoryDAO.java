package dao;

import database.DBConnection;
import model.ConversionHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HistoryDAO.java
 * ----------------
 * Data Access Object for Conversion History operations.
 * Handles all SQL queries for the 'conversion_history' table.
 *
 * OOP Concepts Used:
 *   - Abstraction   : DB operations hidden from service layer
 *   - Encapsulation : All SQL is in one place
 *
 * Author: Rohit Sharma
 */
public class HistoryDAO {

    // -------------------------------------------------------
    // Save a conversion record to the database
    // -------------------------------------------------------
    public boolean saveHistory(ConversionHistory history) {
        String sql = "INSERT INTO conversion_history (user_id, from_currency, to_currency, amount, converted_amount) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, history.getUserId());
            ps.setString(2, history.getFromCurrency());
            ps.setString(3, history.getToCurrency());
            ps.setDouble(4, history.getAmount());
            ps.setDouble(5, history.getConvertedAmount());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error saving history: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Get all conversions for a specific user
    // -------------------------------------------------------
    public List<ConversionHistory> getHistoryByUser(int userId) {
        List<ConversionHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM conversion_history WHERE user_id = ? ORDER BY conversion_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ConversionHistory h = mapRow(rs);
                list.add(h);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching user history: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get ALL conversions — Admin report
    // -------------------------------------------------------
    public List<ConversionHistory> getAllHistory() {
        List<ConversionHistory> list = new ArrayList<>();
        // JOIN with users to also get the user's name
        String sql = "SELECT ch.*, u.name AS user_name "
                   + "FROM conversion_history ch "
                   + "JOIN users u ON ch.user_id = u.id "
                   + "ORDER BY ch.conversion_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ConversionHistory h = mapRow(rs);
                h.setUserName(rs.getString("user_name"));
                list.add(h);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching all history: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get history filtered by currency (for search/filter)
    // -------------------------------------------------------
    public List<ConversionHistory> getHistoryByFilter(int userId, String fromCurrency, String toCurrency) {
        List<ConversionHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM conversion_history WHERE user_id = ? "
                   + "AND from_currency LIKE ? AND to_currency LIKE ? "
                   + "ORDER BY conversion_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, fromCurrency.isEmpty() ? "%" : fromCurrency);
            ps.setString(3, toCurrency.isEmpty()   ? "%" : toCurrency);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error filtering history: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Helper: Map a ResultSet row to a ConversionHistory object
    // -------------------------------------------------------
    private ConversionHistory mapRow(ResultSet rs) throws SQLException {
        ConversionHistory h = new ConversionHistory();
        h.setId(rs.getInt("id"));
        h.setUserId(rs.getInt("user_id"));
        h.setFromCurrency(rs.getString("from_currency"));
        h.setToCurrency(rs.getString("to_currency"));
        h.setAmount(rs.getDouble("amount"));
        h.setConvertedAmount(rs.getDouble("converted_amount"));
        h.setConversionDate(rs.getString("conversion_date"));
        return h;
    }
}
