package com.example.vehiclerentalapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BookingController {
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> idColumn;
    @FXML private TableColumn<Booking, Integer> customerIdColumn;
    @FXML private TableColumn<Booking, Integer> vehicleIdColumn;
    @FXML private TableColumn<Booking, String> startDateColumn;
    @FXML private TableColumn<Booking, String> endDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;

    @FXML private TextField customerIdField;
    @FXML private TextField vehicleIdField;
    @FXML private TextField startDateField;
    @FXML private TextField endDateField;
    @FXML private ChoiceBox<String> statusBox;
    @FXML private TextField searchField;

    private BookingManager bookingManager;
    private ObservableList<Booking> bookingList;

    @FXML
    public void initialize() {
        bookingManager = new BookingManager();
        bookingList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        customerIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("customerId"));
        vehicleIdColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("vehicleId"));
        startDateColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));

        statusBox.getItems().addAll("Active", "Completed", "Cancelled");
        statusBox.setValue("Active");

        refreshTable();

        bookingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                customerIdField.setText(String.valueOf(newSelection.getCustomerId()));
                vehicleIdField.setText(String.valueOf(newSelection.getVehicleId()));
                startDateField.setText(newSelection.getStartDate());
                endDateField.setText(newSelection.getEndDate());
                statusBox.setValue(newSelection.getStatus());
            }
        });
    }

    @FXML
    private void addBooking() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            int vehicleId = Integer.parseInt(vehicleIdField.getText().trim());
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            String status = statusBox.getValue();

            boolean success = bookingManager.addBooking(customerId, vehicleId, startDate, endDate, status);
            if (success) {
                refreshTable();
                clearFields();
                showAlert("Success", "Booking added successfully!");
            } else {
                showAlert("Error", "Failed to add booking. Check input values.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for Customer ID and Vehicle ID.");
        }
    }

    @FXML
    private void updateBooking() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to update.");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            int vehicleId = Integer.parseInt(vehicleIdField.getText().trim());
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            String status = statusBox.getValue();

            boolean success = bookingManager.updateBooking(selectedBooking.getId(), customerId, vehicleId, startDate, endDate, status);
            if (success) {
                refreshTable();
                clearFields();
                showAlert("Success", "Booking updated successfully!");
            } else {
                showAlert("Error", "Failed to update booking. Check input values.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for Customer ID and Vehicle ID.");
        }
    }

    @FXML
    private void deleteBooking() {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to delete.");
            return;
        }

        boolean success = bookingManager.deleteBooking(selectedBooking.getId());
        if (success) {
            refreshTable();
            clearFields();
            showAlert("Success", "Booking deleted successfully!");
        } else {
            showAlert("Error", "Failed to delete booking.");
        }
    }

    @FXML
    private void searchBookings() {
        String keyword = searchField.getText().trim();
        bookingList.clear();
        if (keyword.isEmpty()) {
            bookingList.addAll(bookingManager.getAllBookings());
        } else {
            bookingList.addAll(bookingManager.searchBookings(keyword));
        }
        bookingTable.setItems(bookingList);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refreshTable();
    }

    private void refreshTable() {
        bookingList.clear();
        bookingList.addAll(bookingManager.getAllBookings());
        bookingTable.setItems(bookingList);
    }

    private void clearFields() {
        customerIdField.clear();
        vehicleIdField.clear();
        startDateField.clear();
        endDateField.clear();
        statusBox.setValue("Active");
        bookingTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}