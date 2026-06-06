package controller;

import model.ConversionHistory;
import model.Currency;
import service.CurrencyService;

import java.util.List;

/**
 * CurrencyController.java
 * ------------------------
 * Handles currency conversion and history requests.
 *
 * OOP Concepts Used:
 *   - Abstraction   : Hides CurrencyService logic from callers
 *   - Encapsulation : Controller methods encapsulate request handling
 *
 * Author: Rohit Sharma
 */
public class CurrencyController {

    // Service layer object
    private CurrencyService currencyService = new CurrencyService();

    // -------------------------------------------------------
    // Convert currency and save to history
    // -------------------------------------------------------
    public String convertAndSave(int userId, String fromCode, String toCode, double amount) {

        // Validate amount
        if (amount <= 0) {
            return "ERROR: Amount must be greater than zero.";
        }

        // Validate currency codes
        if (fromCode == null || toCode == null || fromCode.isEmpty() || toCode.isEmpty()) {
            return "ERROR: Please select both currencies.";
        }

        if (fromCode.equalsIgnoreCase(toCode)) {
            return "ERROR: Please select different currencies.";
        }

        // Perform conversion
        double result = currencyService.convertCurrency(fromCode, toCode, amount);

        if (result == -1) {
            return "ERROR: Invalid currency codes. Please try again.";
        }

        // Save to history
        currencyService.saveConversion(userId, fromCode, toCode, amount, result);

        // Return result as a formatted string
        return String.format("SUCCESS: %.4f %s = %.4f %s", amount, fromCode, result, toCode);
    }

    // -------------------------------------------------------
    // Just convert — no history save (for quick preview)
    // -------------------------------------------------------
    public double convert(String fromCode, String toCode, double amount) {
        return currencyService.convertCurrency(fromCode, toCode, amount);
    }

    // -------------------------------------------------------
    // Get all currencies for dropdown menus
    // -------------------------------------------------------
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    // -------------------------------------------------------
    // Get conversion history for a user
    // -------------------------------------------------------
    public List<ConversionHistory> getUserHistory(int userId) {
        return currencyService.getUserHistory(userId);
    }

    // -------------------------------------------------------
    // Filter history
    // -------------------------------------------------------
    public List<ConversionHistory> filterHistory(int userId, String from, String to) {
        return currencyService.filterHistory(userId, from, to);
    }
}
