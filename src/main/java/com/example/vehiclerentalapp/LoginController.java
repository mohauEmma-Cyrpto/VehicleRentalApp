package com.example.vehiclerentalapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private User loggedInUser;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            if (DatabaseSetup.authenticateUser(username, password)) {
                loggedInUser = DatabaseSetup.getUserDetails(username);
                if (loggedInUser != null) {
                    System.out.println("Logged in as: " + loggedInUser.getUsername());
                    loadMainView();
                } else {
                    errorLabel.setText("Failed to retrieve user details.");
                }
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            MainController mainController = loader.getController();
            mainController.setLoggedInUser(loggedInUser);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Vehicle Rental System - Main");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading main view: " + e.getMessage());
        }
    }
}