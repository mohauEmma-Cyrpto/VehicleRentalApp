package com.example.vehiclerentalapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportController {
    @FXML private TextField customerIdField;
    @FXML private TextField revenueStartDateField;
    @FXML private TextField revenueEndDateField;
    @FXML private TextField revenueYearField;
    @FXML private TextField bookingsStartDateField;
    @FXML private TextField bookingsEndDateField;
    @FXML private TextArea reportArea;

    @FXML private PieChart vehicleDistributionChart;
    @FXML private BarChart<String, Number> revenueByMonthChart;
    @FXML private LineChart<String, Number> bookingsOverTimeChart;

    private ReportManager reportManager;

    @FXML
    public void initialize() {
        reportManager = new ReportManager();

        // Set default dates for revenue and bookings fields
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        revenueStartDateField.setText(oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        revenueEndDateField.setText(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookingsStartDateField.setText(oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        bookingsEndDateField.setText(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        revenueYearField.setText(String.valueOf(today.getYear()));
    }

    @FXML
    private void generateAvailableVehiclesReport() {
        String report = reportManager.generateAvailableVehiclesReport();
        reportArea.setText(report);
    }

    @FXML
    private void generateCustomerRentalHistoryReport() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            String report = reportManager.generateCustomerRentalHistoryReport(customerId);
            reportArea.setText(report);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid Customer ID (numeric value).");
        }
    }

    @FXML
    private void generateRevenueReport() {
        String startDate = revenueStartDateField.getText().trim();
        String endDate = revenueEndDateField.getText().trim();
        if (startDate.isEmpty() || endDate.isEmpty()) {
            showAlert("Error", "Please enter both start and end dates.");
            return;
        }
        String report = reportManager.generateRevenueReport(startDate, endDate);
        reportArea.setText(report);
    }

    @FXML
    private void showVehicleDistributionChart() {
        // Hide other charts
        revenueByMonthChart.setVisible(false);
        bookingsOverTimeChart.setVisible(false);

        // Show Pie Chart
        Map<String, Integer> distribution = reportManager.getVehicleDistributionByCategory();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        distribution.forEach((category, count) -> pieChartData.add(new PieChart.Data(category, count)));
        vehicleDistributionChart.setData(pieChartData);
        vehicleDistributionChart.setTitle("Vehicle Distribution by Category");
        vehicleDistributionChart.setVisible(true);
    }

    @FXML
    private void showRevenueByMonthChart() {
        // Hide other charts
        vehicleDistributionChart.setVisible(false);
        bookingsOverTimeChart.setVisible(false);

        // Show Bar Chart
        String year = revenueYearField.getText().trim();
        if (year.isEmpty()) {
            showAlert("Error", "Please enter a year.");
            return;
        }
        Map<String, Double> revenueByMonth = reportManager.getRevenueByMonth(year);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue ($)");
        revenueByMonth.forEach((month, revenue) -> series.getData().add(new XYChart.Data<>(month, revenue)));
        revenueByMonthChart.getData().clear();
        revenueByMonthChart.getData().add(series);
        revenueByMonthChart.setTitle("Revenue by Month (" + year + ")");
        revenueByMonthChart.setVisible(true);
    }

    @FXML
    private void showBookingsOverTimeChart() {
        // Hide other charts
        vehicleDistributionChart.setVisible(false);
        revenueByMonthChart.setVisible(false);

        // Show Line Chart
        String startDate = bookingsStartDateField.getText().trim();
        String endDate = bookingsEndDateField.getText().trim();
        if (startDate.isEmpty() || endDate.isEmpty()) {
            showAlert("Error", "Please enter both start and end dates.");
            return;
        }
        List<ReportManager.BookingCount> bookingCounts = reportManager.getBookingsOverTime(startDate, endDate);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of Bookings");
        for (ReportManager.BookingCount bc : bookingCounts) {
            series.getData().add(new XYChart.Data<>(bc.getMonth(), bc.getCount()));
        }
        bookingsOverTimeChart.getData().clear();
        bookingsOverTimeChart.getData().add(series);
        bookingsOverTimeChart.setTitle("Bookings Over Time");
        bookingsOverTimeChart.setVisible(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}