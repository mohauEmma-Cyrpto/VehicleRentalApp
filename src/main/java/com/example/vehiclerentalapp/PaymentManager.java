package com.example.vehiclerentalapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PaymentManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final double LATE_FEE_PER_DAY = 10.0;
    private static final double ADDITIONAL_SERVICES_FEE = 20.0;

    private long calculateRentalDays(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        return ChronoUnit.DAYS.between(start, end);
    }

    private long calculateLateDays(String endDate) {
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        LocalDate now = LocalDate.now();
        if (now.isAfter(end)) {
            return ChronoUnit.DAYS.between(end, now);
        }
        return 0;
    }

    private double getVehicleRentalPrice(int vehicleId) {
        String sql = "SELECT rental_price_per_day FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("rental_price_per_day");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private Booking getBooking(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE id = ? AND status = 'Active'";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Booking(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean processPayment(int bookingId, String paymentMethod, boolean includeAdditionalServices) {
        if (!paymentMethod.equals("Cash") && !paymentMethod.equals("Credit Card") && !paymentMethod.equals("Online")) {
            System.err.println("Invalid payment method: " + paymentMethod);
            return false;
        }

        Booking booking = getBooking(bookingId);
        if (booking == null) {
            System.err.println("Booking not found or not active: " + bookingId);
            return false;
        }

        long rentalDays = calculateRentalDays(booking.getStartDate(), booking.getEndDate());
        double rentalPricePerDay = getVehicleRentalPrice(booking.getVehicleId());
        double baseAmount = rentalDays * rentalPricePerDay;

        double additionalServicesFee = includeAdditionalServices ? ADDITIONAL_SERVICES_FEE : 0.0;
        long lateDays = calculateLateDays(booking.getEndDate());
        double lateFee = lateDays * LATE_FEE_PER_DAY;
        double totalAmount = baseAmount + additionalServicesFee + lateFee;

        String sql = "INSERT INTO payments (booking_id, amount, payment_method, additional_services_fee, late_fee, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setDouble(2, baseAmount);
            pstmt.setString(3, paymentMethod);
            pstmt.setDouble(4, additionalServicesFee);
            pstmt.setDouble(5, lateFee);
            pstmt.setDouble(6, totalAmount);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getDouble("additional_services_fee"),
                        rs.getDouble("late_fee")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public List<Payment> searchPayments(String keyword) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE booking_id LIKE ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_method"),
                        rs.getDouble("additional_services_fee"),
                        rs.getDouble("late_fee")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public String generateInvoice(int paymentId) {
        StringBuilder invoice = new StringBuilder();
        String sql = "SELECT p.*, b.customer_id, b.vehicle_id, b.start_date, b.end_date, v.brand, v.model, v.rental_price_per_day, c.name " +
                "FROM payments p " +
                "JOIN bookings b ON p.booking_id = b.id " +
                "JOIN vehicles v ON b.vehicle_id = v.id " +
                "JOIN customers c ON b.customer_id = c.id " +
                "WHERE p.id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                invoice.append("========== Vehicle Rental Invoice ==========\n");
                invoice.append("Payment ID: ").append(rs.getInt("id")).append("\n");
                invoice.append("Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
                invoice.append("--------------------------------------------\n");
                invoice.append("Customer: ").append(rs.getString("name")).append("\n");
                invoice.append("Booking ID: ").append(rs.getInt("booking_id")).append("\n");
                invoice.append("Vehicle: ").append(rs.getString("brand")).append(" ").append(rs.getString("model")).append("\n");
                invoice.append("Rental Period: ").append(rs.getString("start_date")).append(" to ").append(rs.getString("end_date")).append("\n");
                invoice.append("--------------------------------------------\n");
                invoice.append("Base Rental Fee: $").append(String.format("%.2f", rs.getDouble("amount"))).append("\n");
                invoice.append("Additional Services Fee: $").append(String.format("%.2f", rs.getDouble("additional_services_fee"))).append("\n");
                invoice.append("Late Fee: $").append(String.format("%.2f", rs.getDouble("late_fee"))).append("\n");
                invoice.append("--------------------------------------------\n");
                invoice.append("Total Amount: $").append(String.format("%.2f", rs.getDouble("total_amount"))).append("\n");
                invoice.append("Payment Method: ").append(rs.getString("payment_method")).append("\n");
                invoice.append("Payment Date: ").append(rs.getTimestamp("payment_date")).append("\n");
                invoice.append("============================================\n");
                invoice.append("Thank you for choosing us!\n");
            } else {
                invoice.append("Payment not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            invoice.append("Error generating invoice: ").append(e.getMessage());
        }
        return invoice.toString();
    }
}