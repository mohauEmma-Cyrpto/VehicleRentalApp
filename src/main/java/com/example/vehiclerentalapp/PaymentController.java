package com.example.vehiclerentalapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PaymentController {
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, Integer> idColumn;
    @FXML private TableColumn<Payment, Integer> bookingIdColumn;
    @FXML private TableColumn<Payment, Double> amountColumn;
    @FXML private TableColumn<Payment, Double> additionalServicesFeeColumn;
    @FXML private TableColumn<Payment, Double> lateFeeColumn;
    @FXML private TableColumn<Payment, Double> totalAmountColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;

    @FXML private TextField bookingIdField;
    @FXML private ChoiceBox<String> paymentMethodBox;
    @FXML private CheckBox additionalServicesCheckBox;
    @FXML private TextField searchField;
    @FXML private TextArea invoiceArea;

    private PaymentManager paymentManager;
    private ObservableList<Payment> paymentList;

    @FXML
    public void initialize() {
        paymentManager = new PaymentManager();
        paymentList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        additionalServicesFeeColumn.setCellValueFactory(new PropertyValueFactory<>("additionalServicesFee"));
        lateFeeColumn.setCellValueFactory(new PropertyValueFactory<>("lateFee"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        refreshTable();

        paymentMethodBox.getItems().addAll("Cash", "Credit Card", "Online");
        paymentMethodBox.setValue("Cash");

        paymentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bookingIdField.setText(String.valueOf(newSelection.getBookingId()));
                paymentMethodBox.setValue(newSelection.getPaymentMethod());
                additionalServicesCheckBox.setSelected(newSelection.getAdditionalServicesFee() > 0);
            }
        });
    }

    @FXML
    private void processPayment() {
        try {
            int bookingId = Integer.parseInt(bookingIdField.getText().trim());
            String paymentMethod = paymentMethodBox.getValue();
            boolean includeAdditionalServices = additionalServicesCheckBox.isSelected();

            boolean success = paymentManager.processPayment(bookingId, paymentMethod, includeAdditionalServices);
            if (success) {
                refreshTable();
                clearFields();
                showAlert("Success", "Payment processed successfully!");
            } else {
                showAlert("Error", "Failed to process payment. Check if the booking exists and is active.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Booking ID (numeric value).");
        }
    }

    @FXML
    private void searchPayments() {
        String keyword = searchField.getText().trim();
        paymentList.clear();
        if (keyword.isEmpty()) {
            paymentList.addAll(paymentManager.getAllPayments());
        } else {
            paymentList.addAll(paymentManager.searchPayments(keyword));
        }
        paymentTable.setItems(paymentList);
    }

    @FXML
    private void clearSearch() {
        searchField.clear();
        refreshTable();
    }

    @FXML
    private void generateInvoice() {
        Payment selectedPayment = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedPayment == null) {
            showAlert("Error", "Please select a payment to generate an invoice.");
            return;
        }

        String invoice = paymentManager.generateInvoice(selectedPayment.getId());
        invoiceArea.setText(invoice);
    }

    private void refreshTable() {
        paymentList.clear();
        paymentList.addAll(paymentManager.getAllPayments());
        paymentTable.setItems(paymentList);
    }

    private void clearFields() {
        bookingIdField.clear();
        paymentMethodBox.setValue("Cash");
        additionalServicesCheckBox.setSelected(false);
        invoiceArea.clear();
        paymentTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}