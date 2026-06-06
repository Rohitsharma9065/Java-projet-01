-- ============================================================
-- Global Currency Converter - Database Script
-- Author: Rohit Sharma
-- Description: Creates database, tables, and inserts sample data
-- ============================================================

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS currency_converter;
USE currency_converter;

-- ============================================================
-- TABLE: users
-- Stores registered user information
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('user', 'admin') DEFAULT 'user',
    last_login  DATETIME DEFAULT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: currencies
-- Stores currency codes, names, and exchange rates (base: USD)
-- ============================================================
CREATE TABLE IF NOT EXISTS currencies (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    currency_code   VARCHAR(10) NOT NULL UNIQUE,
    currency_name   VARCHAR(100) NOT NULL,
    exchange_rate   DECIMAL(15, 6) NOT NULL,   -- Rate relative to USD
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: conversion_history
-- Stores every conversion performed by users
-- ============================================================
CREATE TABLE IF NOT EXISTS conversion_history (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    from_currency    VARCHAR(10) NOT NULL,
    to_currency      VARCHAR(10) NOT NULL,
    amount           DECIMAL(15, 4) NOT NULL,
    converted_amount DECIMAL(15, 4) NOT NULL,
    conversion_date  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- INSERT: Default admin user
-- Password: admin123 (store hashed in production)
-- ============================================================
INSERT INTO users (name, email, password, role) VALUES
('Admin', 'admin@currency.com', 'admin123', 'admin');

-- ============================================================
-- INSERT: Sample currencies (Base currency: USD = 1.0)
-- ============================================================
INSERT INTO currencies (currency_code, currency_name, exchange_rate) VALUES
('USD', 'US Dollar',           1.000000),
('INR', 'Indian Rupee',       83.500000),
('EUR', 'Euro',                0.920000),
('GBP', 'British Pound',      0.790000),
('JPY', 'Japanese Yen',     157.500000),
('AUD', 'Australian Dollar',   1.540000),
('CAD', 'Canadian Dollar',     1.370000),
('CHF', 'Swiss Franc',         0.900000),
('CNY', 'Chinese Yuan',        7.250000),
('AED', 'UAE Dirham',          3.670000),
('SGD', 'Singapore Dollar',    1.350000),
('MYR', 'Malaysian Ringgit',   4.720000);

-- ============================================================
-- Verify the tables
-- ============================================================
SELECT 'Database setup complete!' AS Status;
SELECT * FROM users;
SELECT * FROM currencies;
