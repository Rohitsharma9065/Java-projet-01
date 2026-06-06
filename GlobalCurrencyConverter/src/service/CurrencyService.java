package service;

import dao.CurrencyDAO;
import dao.HistoryDAO;
import model.ConversionHistory;
import model.Currency;

import java.util.List;

/**
 * CurrencyService.java
 * ---------------------
 * Business logic layer for currency conversion.
 * Connects Controller ↔ DAO layers.
 *
 * OOP Concepts Used:
 *   - Encapsulation  : Logic hidden inside methods
 *   - Abstraction    : Controller doesn't know how conversion works
 *   - Polymorphism   : Uses Currency objects generically
 *
 * Conversion Formula:
 *   1. Convert fromCurrency → USD:  amountInUSD = amount / fromRate
 *   2. Convert USD → toCurrency:    result      = amountInUSD * toRate
 *
 * Author: Rohit Sharma
 */
public class CurrencyService {

    // Dependencies — DAO objects to talk to the database
    private CurrencyDAO currencyDAO = new CurrencyDAO();
    private HistoryDAO  historyDAO  = new HistoryDAO();

    // -------------------------------------------------------
    // Perform a currency conversion
    // Returns the converted amount, or -1 if something goes wrong
    // -------------------------------------------------------
    public double convertCurrency(String fromCode, String toCode, double amount) {
        // Step 1: Fetch both currencies from the database
        Currency fromCurrency = currencyDAO.getCurrencyByCode(fromCode);
        Currency toCurrency   = currencyDAO.getCurrencyByCode(toCode);

        // Step 2: Validate that both currencies exist
        if (fromCurrency == null || toCurrency == null) {
            System.out.println("❌ Invalid currency code provided.");
            return -1;
        }

        // Step 3: Apply conversion formula
        // (All rates are relative to USD)
        double amountInUSD  = amount / fromCurrency.getExchangeRate();
        double convertedAmt = amountInUSD * toCurrency.getExchangeRate();

        // Step 4: Round to 4 decimal places
        return Math.round(convertedAmt * 10000.0) / 10000.0;
    }

    // -------------------------------------------------------
    // Save a conversion to history
    // -------------------------------------------------------
    public boolean saveConversion(int userId, String fromCode, String toCode,
                                   double amount, double convertedAmount) {
        ConversionHistory history = new ConversionHistory(
            userId, fromCode, toCode, amount, convertedAmount
        );
        return historyDAO.saveHistory(history);
    }

    // -------------------------------------------------------
    // Get all available currencies
    // -------------------------------------------------------
    public List<Currency> getAllCurrencies() {
        return currencyDAO.getAllCurrencies();
    }

    // -------------------------------------------------------
    // Get a currency by its code
    // -------------------------------------------------------
    public Currency getCurrencyByCode(String code) {
        return currencyDAO.getCurrencyByCode(code);
    }

    // -------------------------------------------------------
    // Add a new currency (Admin)
    // -------------------------------------------------------
    public boolean addCurrency(String code, String name, double rate) {
        // Check if currency already exists
        if (currencyDAO.currencyExists(code)) {
            System.out.println("⚠️ Currency already exists: " + code);
            return false;
        }
        Currency c = new Currency(code.toUpperCase(), name, rate);
        return currencyDAO.addCurrency(c);
    }

    // -------------------------------------------------------
    // Update exchange rate (Admin)
    // -------------------------------------------------------
    public boolean updateRate(String code, double newRate) {
        return currencyDAO.updateExchangeRate(code, newRate);
    }

    // -------------------------------------------------------
    // Delete a currency (Admin)
    // -------------------------------------------------------
    public boolean deleteCurrency(String code) {
        return currencyDAO.deleteCurrency(code);
    }

    // -------------------------------------------------------
    // Get conversion history for a user
    // -------------------------------------------------------
    public List<ConversionHistory> getUserHistory(int userId) {
        return historyDAO.getHistoryByUser(userId);
    }

    // -------------------------------------------------------
    // Get all conversion history (Admin report)
    // -------------------------------------------------------
    public List<ConversionHistory> getAllHistory() {
        return historyDAO.getAllHistory();
    }

    // -------------------------------------------------------
    // Filter history (User dashboard filter)
    // -------------------------------------------------------
    public List<ConversionHistory> filterHistory(int userId, String from, String to) {
        return historyDAO.getHistoryByFilter(userId, from, to);
    }
}
