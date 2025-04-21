package com.example.vehiclerentalapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class MainController {
    @FXML private TabPane mainTabPane;
    @FXML private Tab vehiclesTab;
    @FXML private Tab customersTab;
    @FXML private Tab bookingsTab;
    @FXML private Tab paymentsTab;
    @FXML private Tab reportsTab;
    @FXML private Tab usersTab;

    private User loggedInUser;
    private UserManager userManager;

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        this.userManager = new UserManager(loggedInUser);
        initializeUserManagement();
        restrictAccessBasedOnRole();
    }

    private void initializeUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserManagementView.fxml"));
            usersTab.setContent(loader.load());
            UserManagementController controller = loader.getController();
            controller.setUserManager(userManager);
        } catch (IOException e) {
            System.out.println("Error initializing user management: " + e.getMessage());
        }
    }

    private void restrictAccessBasedOnRole() {
        if (loggedInUser.getRole().equals("Employee")) {
            vehiclesTab.setDisable(true);
            customersTab.setDisable(true);
            reportsTab.setDisable(true);
            usersTab.setDisable(true);
        }
    }

    @FXML
    private void generateVehicleReport() {
        if (!loggedInUser.getRole().equals("Admin")) return;
        exportToCSV("vehicle_report.csv", "SELECT * FROM vehicles");
    }

    @FXML
    private void generateCustomerReport() {
        if (!loggedInUser.getRole().equals("Admin")) return;
        exportToCSV("customer_report.csv", "SELECT * FROM customers");
    }

    @FXML
    private void generateBookingReport() {
        if (!loggedInUser.getRole().equals("Admin")) return;
        exportToCSV("booking_report.csv", "SELECT * FROM bookings");
    }

    private void exportToCSV(String fileName, String query) {
        try (Connection conn = DatabaseSetup.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter writer = new FileWriter(fileName);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(rs.getMetaData()))) {
            while (rs.next()) {
                csvPrinter.printRecord(getRowValues(rs));
            }
            System.out.println("CSV exported to " + fileName);
        } catch (SQLException | IOException e) {
            System.out.println("Error exporting to CSV: " + e.getMessage());
        }
    }

    private String[] getRowValues(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] values = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            values[i - 1] = rs.getString(i);
        }
        return values;
    }
}