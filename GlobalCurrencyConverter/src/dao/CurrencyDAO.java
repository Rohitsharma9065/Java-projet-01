package dao;

import database.DBConnection;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CurrencyDAO.java
 * -----------------
 * Data Access Object for Currency-related database operations.
 * Handles all SQL queries for the 'currencies' table.
 *
 * OOP Concepts Used:
 *   - Abstraction   : Hides SQL complexity
 *   - Encapsulation : SQL logic separated from business logic
 *
 * Author: Rohit Sharma
 */
public class CurrencyDAO {

    // -------------------------------------------------------
    // Get all currencies from the database
    // -------------------------------------------------------
    public List<Currency> getAllCurrencies() {
        List<Currency> list = new ArrayList<>();
        String sql = "SELECT * FROM currencies ORDER BY currency_code ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Currency c = new Currency();
                c.setId(rs.getInt("id"));
                c.setCurrencyCode(rs.getString("currency_code"));
                c.setCurrencyName(rs.getString("currency_name"));
                c.setExchangeRate(rs.getDouble("exchange_rate"));
                list.add(c);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching currencies: " + e.getMessage());
        }
        return list;
    }

    // -------------------------------------------------------
    // Get a single currency by its code (e.g., "USD")
    // -------------------------------------------------------
    public Currency getCurrencyByCode(String code) {
        String sql = "SELECT * FROM currencies WHERE currency_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Currency c = new Currency();
                c.setId(rs.getInt("id"));
                c.setCurrencyCode(rs.getString("currency_code"));
                c.setCurrencyName(rs.getString("currency_name"));
                c.setExchangeRate(rs.getDouble("exchange_rate"));
                return c;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error fetching currency: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------
    // Add a new currency (Admin feature)
    // -------------------------------------------------------
    public boolean addCurrency(Currency currency) {
        String sql = "INSERT INTO currencies (currency_code, currency_name, exchange_rate) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currency.getCurrencyCode().toUpperCase());
            ps.setString(2, currency.getCurrencyName());
            ps.setDouble(3, currency.getExchangeRate());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error adding currency: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Update exchange rate for a currency (Admin feature)
    // -------------------------------------------------------
    public boolean updateExchangeRate(String code, double newRate) {
        String sql = "UPDATE currencies SET exchange_rate = ? WHERE currency_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newRate);
            ps.setString(2, code);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error updating exchange rate: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Delete a currency by its code (Admin feature)
    // -------------------------------------------------------
    public boolean deleteCurrency(String code) {
        String sql = "DELETE FROM currencies WHERE currency_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error deleting currency: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------
    // Check if a currency code already exists
    // -------------------------------------------------------
    public boolean currencyExists(String code) {
        String sql = "SELECT id FROM currencies WHERE currency_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("❌ Error checking currency: " + e.getMessage());
        }
        return false;
    }
}
