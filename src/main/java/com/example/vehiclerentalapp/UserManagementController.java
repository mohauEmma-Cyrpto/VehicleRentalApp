package com.example.vehiclerentalapp;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserManagementController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;

    private UserManager userManager;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @FXML
    private void addUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            userManager.addUser(username, password, role);
            statusLabel.setText("User added successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void updateUser() {
        statusLabel.setText("Update functionality requires selecting a user (not implemented).");
    }

    @FXML
    private void deleteUser() {
        statusLabel.setText("Delete functionality requires selecting a user (not implemented).");
    }
}