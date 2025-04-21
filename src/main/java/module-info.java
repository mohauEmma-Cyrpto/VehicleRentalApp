module com.example.vehiclerentalapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.csv; // For CSV export

    opens com.example.vehiclerentalapp to javafx.fxml;
    exports com.example.vehiclerentalapp;
}
