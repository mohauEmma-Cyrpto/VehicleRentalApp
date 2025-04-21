package com.example.vehiclerentalapp;

public class Customer {
    private int id;
    private String name;
    private String contactInfo;
    private String drivingLicenseNumber;
    private String rentalHistory;

    public Customer(int id, String name, String contactInfo, String drivingLicenseNumber, String rentalHistory) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.rentalHistory = rentalHistory;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getRentalHistory() {
        return rentalHistory;
    }

    public void setRentalHistory(String rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    @Override
    public String toString() {
        return name + " (" + contactInfo + ")";
    }
}