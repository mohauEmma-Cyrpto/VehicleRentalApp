package com.example.vehiclerentalapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VehicleRentalApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseSetup.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(VehicleRentalApp.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Vehicle Rental System - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}