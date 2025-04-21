package com.example.vehiclerentalapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleController {
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> idColumn;
    @FXML private TableColumn<Vehicle, String> brandColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> categoryColumn;
    @FXML private TableColumn<Vehicle, Double> rentalPriceColumn;
    @FXML private TableColumn<Vehicle, String> availabilityColumn;

    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField categoryField;
    @FXML private TextField rentalPriceField;
    @FXML private ChoiceBox<String> availabilityBox;
    @FXML private TextField searchField;

    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        vehicleList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        brandColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("model"));
        categoryColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));
        rentalPriceColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalPricePerDay"));
        availabilityColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("availabilityStatus"));

        availabilityBox.getItems().addAll("Available", "Rented");
        availabilityBox.setValue("Available");

        refreshTable();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                brandField.setText(newSelection.getBrand());
                modelField.setText(newSelection.getModel());
                categoryField.setText(newSelection.getCategory());
                rentalPriceField.setText(String.valueOf(newSelection.getRentalPricePerDay()));
                availabilityBox.setValue(newSelection.getAvailabilityStatus());
            }
        });
    }

    @FXML
    private void addVehicle() {
        String sql = "INSERT INTO vehicles (brand, model, category, rental_price_per_day, availability_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brandField.getText().trim());
            pstmt.setString(2, modelField.getText().trim());
            pstmt.setString(3, categoryField.getText().trim());
            pstmt.setDouble(4, Double.parseDouble(rentalPriceField.getText().trim()));
            pstmt.setString(5, availabilityBox.getValue());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Vehicle added successfully!");
        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to add vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void updateVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert("Error", "Please select a vehicle to update.");
            return;
        }

        String sql = "UPDATE vehicles SET brand = ?, model = ?, category = ?, rental_price_per_day = ?, availability_status = ? WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brandField.getText().trim());
            pstmt.setString(2, modelField.getText().trim());
            pstmt.setString(3, categoryField.getText().trim());
            pstmt.setDouble(4, Double.parseDouble(rentalPriceField.getText().trim()));
            pstmt.setString(5, availabilityBox.getValue());
            pstmt.setInt(6, selectedVehicle.getId());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Vehicle updated successfully!");
        } catch (SQLException | NumberFormatException e) {
            showAlert("Error", "Failed to update vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void deleteVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert("Error", "Please select a vehicle to delete.");
            return;
        }

        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedVehicle.getId());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Vehicle deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete vehicle: " + e.getMessage());
        }
    }

    @FXML
    private void searchVehicles() {
        String keyword = searchField.getText().trim();
        vehicleList.clear();
        String sql = "SELECT * FROM vehicles WHERE brand LIKE ? OR model LIKE ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vehicleList.add(new Vehicle(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price_per_day"),
                        rs.getString("availability_status")));
            }
            vehicleTable.setItems(vehicleList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to search vehicles: " + e.getMessage());
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refreshTable();
    }

    private void refreshTable() {
        vehicleList.clear();
        String sql = "SELECT * FROM vehicles";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vehicleList.add(new Vehicle(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price_per_day"),
                        rs.getString("availability_status")));
            }
            vehicleTable.setItems(vehicleList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load vehicles: " + e.getMessage());
        }
    }

    private void clearFields() {
        brandField.clear();
        modelField.clear();
        categoryField.clear();
        rentalPriceField.clear();
        availabilityBox.setValue("Available");
        vehicleTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}