package com.example.vehiclerentalapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerController {
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> contactInfoColumn;
    @FXML private TableColumn<Customer, String> drivingLicenseColumn;
    @FXML private TableColumn<Customer, String> rentalHistoryColumn;

    @FXML private TextField nameField;
    @FXML private TextField contactInfoField;
    @FXML private TextField drivingLicenseField;
    @FXML private TextField rentalHistoryField;
    @FXML private TextField searchField;

    private ObservableList<Customer> customerList;

    @FXML
    public void initialize() {
        customerList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        contactInfoColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("contactInfo"));
        drivingLicenseColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("drivingLicenseNumber"));
        rentalHistoryColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalHistory"));

        refreshTable();

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                contactInfoField.setText(newSelection.getContactInfo());
                drivingLicenseField.setText(newSelection.getDrivingLicenseNumber());
                rentalHistoryField.setText(newSelection.getRentalHistory());
            }
        });
    }

    @FXML
    private void addCustomer() {
        String sql = "INSERT INTO customers (name, contact_info, driving_license_number, rental_history) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, contactInfoField.getText().trim());
            pstmt.setString(3, drivingLicenseField.getText().trim());
            pstmt.setString(4, rentalHistoryField.getText().trim());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Customer added successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add customer: " + e.getMessage());
        }
    }

    @FXML
    private void updateCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer to update.");
            return;
        }

        String sql = "UPDATE customers SET name = ?, contact_info = ?, driving_license_number = ?, rental_history = ? WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, contactInfoField.getText().trim());
            pstmt.setString(3, drivingLicenseField.getText().trim());
            pstmt.setString(4, rentalHistoryField.getText().trim());
            pstmt.setInt(5, selectedCustomer.getId());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Customer updated successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to update customer: " + e.getMessage());
        }
    }

    @FXML
    private void deleteCustomer() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert("Error", "Please select a customer to delete.");
            return;
        }

        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedCustomer.getId());
            pstmt.executeUpdate();
            refreshTable();
            clearFields();
            showAlert("Success", "Customer deleted successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete customer: " + e.getMessage());
        }
    }

    @FXML
    private void searchCustomers() {
        String keyword = searchField.getText().trim();
        customerList.clear();
        String sql = "SELECT * FROM customers WHERE name LIKE ? OR contact_info LIKE ?";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")));
            }
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to search customers: " + e.getMessage());
        }
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refreshTable();
    }

    private void refreshTable() {
        customerList.clear();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customerList.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("driving_license_number"),
                        rs.getString("rental_history")));
            }
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        contactInfoField.clear();
        drivingLicenseField.clear();
        rentalHistoryField.clear();
        customerTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}