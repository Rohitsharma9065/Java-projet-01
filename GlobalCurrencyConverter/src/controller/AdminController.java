package controller;

import model.ConversionHistory;
import model.Currency;
import model.User;
import service.CurrencyService;
import service.UserService;

import java.util.List;

/**
 * AdminController.java
 * ---------------------
 * Handles all admin panel operations:
 *   - Manage currencies (add, update, delete)
 *   - View all users
 *   - View all conversion reports
 *
 * OOP Concepts Used:
 *   - Inheritance    : Could extend a BaseController (shown below)
 *   - Encapsulation  : Admin logic separated from user logic
 *   - Abstraction    : Uses service layer
 *
 * Author: Rohit Sharma
 */
public class AdminController {

    // Service layer objects
    private CurrencyService currencyService = new CurrencyService();
    private UserService     userService     = new UserService();

    // -------------------------------------------------------
    // Add a new currency
    // -------------------------------------------------------
    public String addCurrency(String code, String name, double rate) {

        // Validate input
        if (code == null || code.trim().isEmpty()) return "ERROR: Currency code is required.";
        if (name == null || name.trim().isEmpty()) return "ERROR: Currency name is required.";
        if (rate <= 0) return "ERROR: Exchange rate must be positive.";

        boolean success = currencyService.addCurrency(code.trim().toUpperCase(), name.trim(), rate);
        return success ? "SUCCESS: Currency added successfully."
                       : "ERROR: Currency already exists or could not be added.";
    }

    // -------------------------------------------------------
    // Update exchange rate for a currency
    // -------------------------------------------------------
    public String updateExchangeRate(String code, double newRate) {

        if (code == null || code.trim().isEmpty()) return "ERROR: Currency code is required.";
        if (newRate <= 0) return "ERROR: Rate must be a positive number.";

        boolean success = currencyService.updateRate(code.trim().toUpperCase(), newRate);
        return success ? "SUCCESS: Exchange rate updated."
                       : "ERROR: Currency not found.";
    }

    // -------------------------------------------------------
    // Delete a currency
    // -------------------------------------------------------
    public String deleteCurrency(String code) {
        if (code == null || code.trim().isEmpty()) return "ERROR: Currency code is required.";

        boolean success = currencyService.deleteCurrency(code.trim().toUpperCase());
        return success ? "SUCCESS: Currency deleted."
                       : "ERROR: Could not delete currency.";
    }

    // -------------------------------------------------------
    // Get all currencies (for admin table view)
    // -------------------------------------------------------
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    // -------------------------------------------------------
    // Get all users
    // -------------------------------------------------------
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // -------------------------------------------------------
    // Get full conversion report
    // -------------------------------------------------------
    public List<ConversionHistory> getConversionReport() {
        return currencyService.getAllHistory();
    }
}
