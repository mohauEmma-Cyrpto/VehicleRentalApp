package com.example.vehiclerentalapp;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Available Vehicles Report
    public String generateAvailableVehiclesReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== Available Vehicles Report ==========\n");
        report.append("Generated on: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        report.append("--------------------------------------------\n");

        String sql = "SELECT * FROM vehicles WHERE availability_status = 'Available'";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            boolean hasVehicles = false;
            while (rs.next()) {
                hasVehicles = true;
                report.append("ID: ").append(rs.getInt("id")).append("\n");
                report.append("Brand: ").append(rs.getString("brand")).append("\n");
                report.append("Model: ").append(rs.getString("model")).append("\n");
                report.append("Category: ").append(rs.getString("category")).append("\n");
                report.append("Price/Day: $").append(rs.getDouble("rental_price_per_day")).append("\n");
                report.append("--------------------------------------------\n");
            }
            if (!hasVehicles) {
                report.append("No available vehicles found.\n");
            }
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage()).append("\n");
        }
        report.append("============================================\n");
        return report.toString();
    }

    // Customer Rental History Report
    public String generateCustomerRentalHistoryReport(int customerId) {
        StringBuilder report = new StringBuilder();
        report.append("========== Customer Rental History Report ==========\n");
        report.append("Customer ID: ").append(customerId).append("\n");
        report.append("Generated on: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        report.append("--------------------------------------------\n");

        String sql = "SELECT b.*, v.brand, v.model, c.name " +
                "FROM bookings b " +
                "JOIN vehicles v ON b.vehicle_id = v.id " +
                "JOIN customers c ON b.customer_id = c.id " +
                "WHERE b.customer_id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            boolean hasRentals = false;
            while (rs.next()) {
                hasRentals = true;
                report.append("Booking ID: ").append(rs.getInt("id")).append("\n");
                report.append("Customer: ").append(rs.getString("name")).append("\n");
                report.append("Vehicle: ").append(rs.getString("brand")).append(" ").append(rs.getString("model")).append("\n");
                report.append("Start Date: ").append(rs.getString("start_date")).append("\n");
                report.append("End Date: ").append(rs.getString("end_date")).append("\n");
                report.append("Status: ").append(rs.getString("status")).append("\n");
                report.append("--------------------------------------------\n");
            }
            if (!hasRentals) {
                report.append("No rental history found for Customer ID: ").append(customerId).append("\n");
            }
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage()).append("\n");
        }
        report.append("============================================\n");
        return report.toString();
    }

    // Revenue Report
    public String generateRevenueReport(String startDate, String endDate) {
        StringBuilder report = new StringBuilder();
        report.append("========== Revenue Report ==========\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
        report.append("Generated on: ").append(LocalDate.now().format(DATE_FORMATTER)).append("\n");
        report.append("--------------------------------------------\n");

        String sql = "SELECT * FROM payments WHERE payment_date BETWEEN ? AND ?";
        double totalRevenue = 0.0;
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate + " 23:59:59");
            ResultSet rs = pstmt.executeQuery();
            boolean hasPayments = false;
            while (rs.next()) {
                hasPayments = true;
                double totalAmount = rs.getDouble("total_amount");
                totalRevenue += totalAmount;
                report.append("Payment ID: ").append(rs.getInt("id")).append("\n");
                report.append("Booking ID: ").append(rs.getInt("booking_id")).append("\n");
                report.append("Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n");
                report.append("Payment Method: ").append(rs.getString("payment_method")).append("\n");
                report.append("Payment Date: ").append(rs.getTimestamp("payment_date")).append("\n");
                report.append("--------------------------------------------\n");
            }
            if (!hasPayments) {
                report.append("No payments found for the selected period.\n");
            } else {
                report.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
            }
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage()).append("\n");
        }
        report.append("============================================\n");
        return report.toString();
    }

    // Data for Pie Chart: Vehicle Distribution by Category
    public Map<String, Integer> getVehicleDistributionByCategory() {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as count FROM vehicles GROUP BY category";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                distribution.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distribution;
    }

    // Data for Bar Chart: Revenue by Month
    public Map<String, Double> getRevenueByMonth(String year) {
        Map<String, Double> revenueByMonth = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            revenueByMonth.put(String.format("%d-%02d", Integer.parseInt(year), month), 0.0);
        }

        String sql = "SELECT DATE_FORMAT(payment_date, '%Y-%m') as month, SUM(total_amount) as total " +
                "FROM payments " +
                "WHERE YEAR(payment_date) = ? " +
                "GROUP BY DATE_FORMAT(payment_date, '%Y-%m')";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                revenueByMonth.put(month, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenueByMonth;
    }

    // Data for Line Chart: Number of Bookings Over Time
    public List<BookingCount> getBookingsOverTime(String startDate, String endDate) {
        List<BookingCount> bookingCounts = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(start_date, '%Y-%m') as month, COUNT(*) as count " +
                "FROM bookings " +
                "WHERE start_date BETWEEN ? AND ? " +
                "GROUP BY DATE_FORMAT(start_date, '%Y-%m') " +
                "ORDER BY month";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookingCounts.add(new BookingCount(rs.getString("month"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingCounts;
    }

    // Helper class for Line Chart data
    public static class BookingCount {
        private String month;
        private int count;

        public BookingCount(String month, int count) {
            this.month = month;
            this.count = count;
        }

        public String getMonth() {
            return month;
        }

        public int getCount() {
            return count;
        }
    }
}
