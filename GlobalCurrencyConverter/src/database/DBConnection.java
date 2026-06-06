package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * ------------------
 * Handles JDBC connection to MySQL database.
 * Uses Singleton pattern so only ONE connection is created.
 *
 * OOP Concept Used: Encapsulation (private fields, public methods)
 *
 * Author: Rohit Sharma
 */
public class DBConnection {

    // -------------------------------------------------------
    // Database configuration — change these to match your setup
    // -------------------------------------------------------
    private static final String URL      = "jdbc:mysql://localhost:3306/currency_converter";
    private static final String USERNAME = "root";      // your MySQL username
    private static final String PASSWORD = "root";      // your MySQL password

    // Singleton connection instance
    private static Connection connection = null;

    // Private constructor — prevents creating objects outside this class
    private DBConnection() {}

    /**
     * Returns the single database connection.
     * Creates one if it doesn't exist yet (Singleton pattern).
     *
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Step 1: Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Step 2: Create the connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the database connection.
     * Always call this when you're done with the database.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
