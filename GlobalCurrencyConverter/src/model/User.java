package model;

/**
 * User.java
 * ----------
 * Represents a registered user in the system.
 *
 * OOP Concepts Used:
 *   - Encapsulation : All fields are private; accessed via getters/setters
 *   - Abstraction   : Hides internal details, exposes only what's needed
 *
 * Author: Rohit Sharma
 */
public class User {

    // -------------------------------------------------------
    // Private fields — Encapsulation
    // -------------------------------------------------------
    private int    id;
    private String name;
    private String email;
    private String password;
    private String role;         // "user" or "admin"
    private String lastLogin;

    // -------------------------------------------------------
    // Default Constructor
    // -------------------------------------------------------
    public User() {}

    // -------------------------------------------------------
    // Parameterised Constructor
    // -------------------------------------------------------
    public User(int id, String name, String email, String password, String role) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // -------------------------------------------------------
    // Constructor without id (used when registering a new user)
    // -------------------------------------------------------
    public User(String name, String email, String password, String role) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.role     = role;
    }

    // -------------------------------------------------------
    // Getters — Read private fields
    // -------------------------------------------------------
    public int    getId()        { return id; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getRole()      { return role; }
    public String getLastLogin() { return lastLogin; }

    // -------------------------------------------------------
    // Setters — Modify private fields
    // -------------------------------------------------------
    public void setId(int id)              { this.id        = id; }
    public void setName(String name)       { this.name      = name; }
    public void setEmail(String email)     { this.email     = email; }
    public void setPassword(String pw)     { this.password  = pw; }
    public void setRole(String role)       { this.role      = role; }
    public void setLastLogin(String time)  { this.lastLogin = time; }

    // -------------------------------------------------------
    // toString — Useful for printing/debugging
    // -------------------------------------------------------
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email
                + "', role='" + role + "'}";
    }
}
