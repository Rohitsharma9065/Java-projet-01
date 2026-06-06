package model;

/**
 * ConversionHistory.java
 * -----------------------
 * Represents a single currency conversion record.
 *
 * OOP Concepts Used:
 *   - Encapsulation : Private fields with getters/setters
 *
 * Author: Rohit Sharma
 */
public class ConversionHistory {

    // -------------------------------------------------------
    // Private fields — Encapsulation
    // -------------------------------------------------------
    private int    id;
    private int    userId;
    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double convertedAmount;
    private String conversionDate;
    private String userName;   // Extra field for display (JOIN with users table)

    // -------------------------------------------------------
    // Default Constructor
    // -------------------------------------------------------
    public ConversionHistory() {}

    // -------------------------------------------------------
    // Parameterised Constructor
    // -------------------------------------------------------
    public ConversionHistory(int userId, String fromCurrency, String toCurrency,
                             double amount, double convertedAmount) {
        this.userId          = userId;
        this.fromCurrency    = fromCurrency;
        this.toCurrency      = toCurrency;
        this.amount          = amount;
        this.convertedAmount = convertedAmount;
    }

    // -------------------------------------------------------
    // Getters
    // -------------------------------------------------------
    public int    getId()              { return id; }
    public int    getUserId()          { return userId; }
    public String getFromCurrency()    { return fromCurrency; }
    public String getToCurrency()      { return toCurrency; }
    public double getAmount()          { return amount; }
    public double getConvertedAmount() { return convertedAmount; }
    public String getConversionDate()  { return conversionDate; }
    public String getUserName()        { return userName; }

    // -------------------------------------------------------
    // Setters
    // -------------------------------------------------------
    public void setId(int id)                          { this.id              = id; }
    public void setUserId(int userId)                  { this.userId          = userId; }
    public void setFromCurrency(String from)           { this.fromCurrency    = from; }
    public void setToCurrency(String to)               { this.toCurrency      = to; }
    public void setAmount(double amount)               { this.amount          = amount; }
    public void setConvertedAmount(double converted)   { this.convertedAmount = converted; }
    public void setConversionDate(String date)         { this.conversionDate  = date; }
    public void setUserName(String name)               { this.userName        = name; }

    // -------------------------------------------------------
    // toString
    // -------------------------------------------------------
    @Override
    public String toString() {
        return "ConversionHistory{id=" + id + ", from=" + fromCurrency
                + ", to=" + toCurrency + ", amount=" + amount
                + ", converted=" + convertedAmount + ", date=" + conversionDate + "}";
    }
}
