package com.example.vehiclerentalapp;

public class Vehicle {
    private int id;
    private String brand;
    private String model;
    private String category;
    private double rentalPricePerDay;
    private String availabilityStatus;

    public Vehicle(int id, String brand, String model, String category, double rentalPricePerDay, String availabilityStatus) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.rentalPricePerDay = rentalPricePerDay;
        this.availabilityStatus = availabilityStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return brand + " " + model + " (" + category + ")";
    }
}