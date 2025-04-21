package com.example.vehiclerentalapp;

public class Payment {
    private int id;
    private int bookingId;
    private double amount;
    private String paymentMethod;
    private double additionalServicesFee;
    private double lateFee;
    private double totalAmount;

    public Payment(int id, int bookingId, double amount, String paymentMethod, double additionalServicesFee, double lateFee) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.additionalServicesFee = additionalServicesFee;
        this.lateFee = lateFee;
        this.totalAmount = amount + additionalServicesFee + lateFee;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getAdditionalServicesFee() {
        return additionalServicesFee;
    }

    public double getLateFee() {
        return lateFee;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "Payment ID: " + id + " (Booking ID: " + bookingId + ", Total: $" + totalAmount + ")";
    }
}