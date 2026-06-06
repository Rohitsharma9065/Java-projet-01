import controller.AdminController;
import controller.CurrencyController;
import controller.LoginController;
import model.Currency;
import model.User;

import java.util.List;

/**
 * Main.java
 * ----------
 * Entry point of the application.
 * Demonstrates how all components work together.
 * Run this to test the backend logic via console.
 *
 * Author: Rohit Sharma
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("   Global Currency Converter - Backend Test  ");
        System.out.println("==============================================\n");

        // ---- Test 1: User Registration ----
        LoginController loginCtrl = new LoginController();

        System.out.println("--- Test: Register User ---");
        String regResult = loginCtrl.register("Rohit Sharma", "rohit@test.com", "pass123", "pass123");
        System.out.println(regResult);

        // ---- Test 2: User Login ----
        System.out.println("\n--- Test: Login ---");
        User user = loginCtrl.login("rohit@test.com", "pass123");
        if (user != null) {
            System.out.println("Logged in as: " + user.getName() + " | Role: " + user.getRole());
        }

        // ---- Test 3: Admin Login ----
        System.out.println("\n--- Test: Admin Login ---");
        User admin = loginCtrl.login("admin@currency.com", "admin123");
        if (admin != null) {
            System.out.println("Admin logged in: " + admin.getName());
        }

        // ---- Test 4: Get All Currencies ----
        System.out.println("\n--- Test: All Currencies ---");
        CurrencyController currCtrl = new CurrencyController();
        List<Currency> currencies = currCtrl.getAllCurrencies();
        for (Currency c : currencies) {
            System.out.printf("  %-5s | %-25s | Rate: %.4f%n",
                c.getCurrencyCode(), c.getCurrencyName(), c.getExchangeRate());
        }

        // ---- Test 5: Currency Conversion ----
        System.out.println("\n--- Test: Currency Conversions ---");
        if (user != null) {
            String r1 = currCtrl.convertAndSave(user.getId(), "INR", "USD", 1000);
            System.out.println(r1);

            String r2 = currCtrl.convertAndSave(user.getId(), "USD", "EUR", 50);
            System.out.println(r2);

            String r3 = currCtrl.convertAndSave(user.getId(), "GBP", "INR", 10);
            System.out.println(r3);
        }

        // ---- Test 6: Admin — Add Currency ----
        System.out.println("\n--- Test: Admin Add Currency ---");
        AdminController adminCtrl = new AdminController();
        String addResult = adminCtrl.addCurrency("BTC", "Bitcoin", 65000.0);
        System.out.println(addResult);

        // ---- Test 7: Admin — Update Rate ----
        System.out.println("\n--- Test: Update USD Rate ---");
        String updateResult = adminCtrl.updateExchangeRate("USD", 1.0);
        System.out.println(updateResult);

        System.out.println("\n==============================================");
        System.out.println("  All tests completed! Check your database.  ");
        System.out.println("==============================================");
    }
}
