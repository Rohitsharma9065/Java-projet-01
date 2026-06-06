package model;

/**
 * Currency.java
 * --------------
 * Represents a currency with its code, name, and exchange rate.
 *
 * OOP Concepts Used:
 *   - Encapsulation : Private fields with getters/setters
 *   - Abstraction   : Clean interface for currency data
 *
 * Author: Rohit Sharma
 */
public class Currency {

    // -------------------------------------------------------
    // Private fields — Encapsulation
    // -------------------------------------------------------
    private int    id;
    private String currencyCode;   // e.g. "USD", "INR"
    private String currencyName;   // e.g. "US Dollar", "Indian Rupee"
    private double exchangeRate;   // Rate relative to USD (USD = 1.0)

    // -------------------------------------------------------
    // Default Constructor
    // -------------------------------------------------------
    public Currency() {}

    // -------------------------------------------------------
    // Parameterised Constructor
    // -------------------------------------------------------
    public Currency(int id, String currencyCode, String currencyName, double exchangeRate) {
        this.id           = id;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    // -------------------------------------------------------
    // Constructor without id (used when adding new currency)
    // -------------------------------------------------------
    public Currency(String currencyCode, String currencyName, double exchangeRate) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------
    public int    getId()           { return id; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCurrencyName() { return currencyName; }
    public double getExchangeRate() { return exchangeRate; }

    // -------------------------------------------------------
    // Setters
    // -------------------------------------------------------
    public void setId(int id)                       { this.id           = id; }
    public void setCurrencyCode(String code)        { this.currencyCode = code; }
    public void setCurrencyName(String name)        { this.currencyName = name; }
    public void setExchangeRate(double rate)        { this.exchangeRate = rate; }

    // -------------------------------------------------------
    // toString
    // -------------------------------------------------------
    @Override
    public String toString() {
        return "Currency{code='" + currencyCode + "', name='" + currencyName
                + "', rate=" + exchangeRate + "}";
    }
}
