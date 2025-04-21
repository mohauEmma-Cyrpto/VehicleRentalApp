package com.example.vehiclerentalapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
    // Add a new customer to the database
    public void addCustomer(String name, String contactInfo, String drivingLicenseNumber, String rentalHistory) {
        String sql = "INSERT INTO customers (name, contact_info, driving_license_number, rental_history) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contactInfo);
            pstmt.setString(3, drivingLicenseNumber);
            pstmt.setString(4, rentalHistory != null ? rentalHistory : "");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update an existing customer in the database
    public void updateCustomer(int id, String name, String contactInfo, String drivingLicenseNumber, String rentalHistory) {
        String sql = "UPDATE customers SET name = ?, contact_info = ?, driving_license_number = ?, rental_history = ? WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contactInfo);
            pstmt.setString(3, drivingLicenseNumber);
            pstmt.setString(4, rentalHistory != null ? rentalHistory : "");
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a customer from the database by ID
    public void deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search for customers by name, contact info, or driving license number (case-insensitive partial match)
    public List<Customer> searchCustomers(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE LOWER(name) LIKE ? OR LOWER(contact_info) LIKE ? OR LOWER(driving_license_number) LIKE ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Get all customers from the database
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Get a customer by ID (useful for retrieving a customer before updating)
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}