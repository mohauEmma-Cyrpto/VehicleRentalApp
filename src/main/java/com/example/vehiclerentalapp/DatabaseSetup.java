package com.example.vehiclerentalapp;

import java.sql.*;

public class DatabaseSetup {
    private static final String URL = "jdbc:mysql://localhost:3306/vehicle_rental_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // Update if your MySQL password differs

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS vehicle_rental_db");
            stmt.execute("USE vehicle_rental_db");

            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'Employee')");

            // Vehicles table
            stmt.execute("CREATE TABLE IF NOT EXISTS vehicles (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "license_plate VARCHAR(20) NOT NULL, " +
                    "model VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'Available')");

            // Customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL)");

            // Bookings table (rental data)
            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "customer_id INT, " +
                    "vehicle_id INT, " +
                    "start_date DATE, " +
                    "end_date DATE, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(id), " +
                    "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id))");

            // Payments table
            stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "booking_id INT, " +
                    "amount DECIMAL(10, 2), " +
                    "payment_date DATE, " +
                    "FOREIGN KEY (booking_id) REFERENCES bookings(id))");

            // Insert default admin and employee if not exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'Admin')");
            }
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'employee1'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('employee1', 'emp123', 'Employee')");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed. Please check your MySQL server and credentials.", e);
        }
    }

    public static boolean authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static User getUserDetails(String username) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, username, role FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String fetchedUsername = rs.getString("username");
                String role = rs.getString("role");
                return new User(id, fetchedUsername, role);
            }
            return null;
        }
    }
}