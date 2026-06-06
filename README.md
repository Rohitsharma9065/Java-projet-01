# 💱 Global Currency Converter

A full-stack web application built with **Core Java + JDBC + MySQL + HTML/CSS/JavaScript**.  
Architecture: **MVC Pattern** | No Spring Boot — pure Java Servlets.

**Author: Rohit Sharma**

---

## 📁 Project Structure

```
GlobalCurrencyConverter/
│
├── pom.xml                          ← Maven build file
├── database.sql                     ← MySQL setup script
├── README.md                        ← This file
│
├── src/
│   ├── model/
│   │   ├── User.java                ← User entity (Encapsulation)
│   │   ├── Currency.java            ← Currency entity
│   │   └── ConversionHistory.java   ← History entity
│   │
│   ├── dao/
│   │   ├── UserDAO.java             ← DB operations for users
│   │   ├── CurrencyDAO.java         ← DB operations for currencies
│   │   └── HistoryDAO.java          ← DB operations for history
│   │
│   ├── service/
│   │   ├── CurrencyService.java     ← Conversion business logic
│   │   └── UserService.java         ← User business logic
│   │
│   ├── controller/
│   │   ├── LoginController.java     ← Login/Register controller
│   │   ├── CurrencyController.java  ← Conversion controller
│   │   └── AdminController.java     ← Admin controller
│   │
│   ├── servlet/                     ← HTTP layer (new)
│   │   ├── AuthServlet.java         ← /api/auth/*
│   │   ├── CurrencyServlet.java     ← /api/currency/*
│   │   ├── AdminServlet.java        ← /api/admin/*
│   │   └── HistoryServlet.java      ← /api/history/*
│   │
│   ├── database/
│   │   └── DBConnection.java        ← JDBC Singleton connection
│   │
│   └── Main.java                    ← Console test runner
│
└── web/
    ├── index.html                   ← Home page
    ├── login.html                   ← Login page
    ├── register.html                ← Register page
    ├── dashboard.html               ← Converter dashboard
    ├── history.html                 ← Conversion history
    ├── admin.html                   ← Admin panel
    │
    ├── css/
    │   └── style.css                ← All styles + dark mode
    │
    ├── js/
    │   ├── app.js                   ← Main JS (fetch API calls)
    │   └── validation.js            ← Form validation
    │
    └── WEB-INF/
        └── web.xml                  ← Servlet URL mappings
```

---

## ⚙️ Setup Guide (Step by Step)

### Prerequisites
| Tool | Version | Download |
|------|---------|----------|
| JDK  | 11+     | https://adoptium.net |
| Maven | 3.8+  | https://maven.apache.org |
| MySQL | 8.0+  | https://dev.mysql.com/downloads |
| VS Code / IntelliJ | Any | — |

---

### Step 1 — Set up the Database

1. Open **MySQL Workbench** or the MySQL command line.
2. Run the database script:

```sql
source C:/path/to/GlobalCurrencyConverter/database.sql
```

Or paste the contents of `database.sql` into the MySQL shell.

This creates:
- Database: `currency_converter`
- Tables: `users`, `currencies`, `conversion_history`
- Default admin: `admin@currency.com` / `admin123`
- 12 currencies pre-loaded

---

### Step 2 — Configure Database Connection

Open `src/database/DBConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/currency_converter";
private static final String USERNAME = "root";       // ← your MySQL username
private static final String PASSWORD = "your_pass";  // ← your MySQL password
```

---

### Step 3 — Build with Maven

Open a terminal in the project folder:

```bash
cd "c:\Users\sahil\Java project01\GlobalCurrencyConverter"

# Download dependencies + compile + package as WAR
mvn clean package
```

This creates: `target/GlobalCurrencyConverter.war`

---

### Step 4 — Run on Embedded Tomcat

```bash
mvn tomcat7:run
```

Then open your browser: **http://localhost:8080**

---

### Step 5 — Open the App

| Page | URL |
|------|-----|
| Home | http://localhost:8080 |
| Login | http://localhost:8080/login.html |
| Register | http://localhost:8080/register.html |
| Dashboard | http://localhost:8080/dashboard.html |
| History | http://localhost:8080/history.html |
| Admin Panel | http://localhost:8080/admin.html |

---

### Alternative: Open as Static Files (No Tomcat)

If you just want to see the UI without running Java:

1. Open `web/index.html` directly in your browser.
2. The app runs in **demo mode** using `localStorage` instead of MySQL.
3. Demo login: `rohit@test.com` / `pass123` or `admin@currency.com` / `admin123`

---

## 🔑 Default Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@currency.com | admin123 | Admin |
| rohit@test.com | pass123 | User |

---

## 💡 OOP Concepts Used

| Concept | Where Used |
|---------|-----------|
| **Encapsulation** | All model classes (User, Currency, ConversionHistory) with private fields + getters/setters |
| **Inheritance** | All Servlets extend `HttpServlet` |
| **Polymorphism** | `doGet()`/`doPost()` overriding in each Servlet |
| **Abstraction** | Service layer hides DB complexity from Controllers |
| **Interfaces** | Java Servlet API (`HttpServlet` implements `Servlet`) |

---

## 🌐 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/login` | Login user |
| POST | `/api/auth/register` | Register new user |
| GET  | `/api/auth/logout` | Logout |
| GET  | `/api/currency/list` | Get all currencies |
| POST | `/api/currency/convert` | Convert currency |
| GET  | `/api/history/user` | User's history |
| GET  | `/api/history/filter?from=USD&to=INR` | Filtered history |
| GET  | `/api/admin/currencies` | Admin: all currencies |
| POST | `/api/admin/addCurrency` | Admin: add currency |
| POST | `/api/admin/updateRate` | Admin: update rate |
| POST | `/api/admin/deleteCurrency` | Admin: delete currency |
| GET  | `/api/admin/users` | Admin: all users |
| GET  | `/api/admin/reports` | Admin: all conversions |

---

## 🧮 Conversion Formula

```
Converted Amount = (Amount ÷ From_Rate) × To_Rate
```

All rates are stored relative to USD (USD = 1.0).

**Example:** Convert 1000 INR to USD  
`= (1000 ÷ 83.5) × 1.0 = 11.976 USD`

---

## 🖼️ Features

- ✅ User Registration & Login
- ✅ Admin Panel (manage currencies, view users & reports)
- ✅ 12+ currencies supported
- ✅ Live rates from MySQL database
- ✅ Conversion History with filter
- ✅ Export History to CSV
- ✅ Dark Mode toggle
- ✅ Responsive design (mobile friendly)
- ✅ Input validation (frontend + backend)
- ✅ Prepared Statements (SQL Injection safe)
- ✅ MVC Architecture

---

## 📝 Viva Questions & Answers

**Q1: What is JDBC?**  
JDBC (Java Database Connectivity) is an API that allows Java programs to connect to and execute queries on a database using standard SQL.

**Q2: What is a PreparedStatement?**  
A `PreparedStatement` is a pre-compiled SQL query that uses `?` placeholders for user input. It prevents SQL Injection attacks and improves performance.

**Q3: What is MVC?**  
MVC stands for Model-View-Controller. Model = data classes, View = HTML pages, Controller = Java classes that handle requests.

**Q4: What is Encapsulation?**  
Encapsulation is making class fields `private` and providing `public` getters/setters so external code cannot directly change internal state.

**Q5: What is a Servlet?**  
A Servlet is a Java class that handles HTTP requests (GET, POST) on a web server (Tomcat). It extends `HttpServlet` and overrides `doGet()`/`doPost()`.

**Q6: Why use Singleton for DBConnection?**  
To ensure only ONE database connection is created throughout the application's lifetime, saving resources.

**Q7: What is the conversion formula?**  
`result = (amount / fromCurrencyRate) * toCurrencyRate`  
All rates are stored relative to USD.

**Q8: What is a DAO?**  
Data Access Object — a design pattern that separates database operations from business logic.

**Q9: What is polymorphism?**  
The ability of different classes to implement the same method differently. Example: each Servlet overrides `doGet()` and `doPost()` from `HttpServlet` in its own way.

**Q10: How is the app secured against SQL Injection?**  
By using `PreparedStatement` with `?` parameters for all SQL queries. User input never gets directly embedded into SQL strings.

---

© 2026 Global Currency Converter | Made by **Rohit Sharma**

