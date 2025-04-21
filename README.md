# Vehicle Rental App

## Introduction

### Vehicle Rental App is a JavaFX-based application 

It is designed to manage vehicle rentals. It allows users to add, update, and delete vehicles, 
customers, and bookings. The app includes role-based access control, with admins having 
full access   and employees having restricted access.

## Technologies Used

- **Java**: Version 17 or higher
- **MySQL**: A running MySQL server
- **Maven**: For building the project

## Database Setup

Create a database named vehicle_rental_db.

Execute these SQL commands to set up tables:

CREATE TABLE IF NOT EXISTS users (
id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(50) NOT NULL,
password VARCHAR(255) NOT NULL,
role VARCHAR(20) NOT NULL DEFAULT 'Employee'
);

CREATE TABLE IF NOT EXISTS vehicles (
id INT AUTO_INCREMENT PRIMARY KEY,
license_plate VARCHAR(20) NOT NULL,
model VARCHAR(50) NOT NULL,
status VARCHAR(20) DEFAULT 'Available'
);

CREATE TABLE IF NOT EXISTS customers (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
id INT AUTO_INCREMENT PRIMARY KEY,
customer_id INT,
vehicle_id INT,
start_date DATE,
end_date DATE,
FOREIGN KEY (customer_id) REFERENCES customers(id),
FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

CREATE TABLE IF NOT EXISTS payments (
id INT AUTO_INCREMENT PRIMARY KEY,
booking_id INT,
amount DECIMAL(10, 2),
payment_date DATE,
FOREIGN KEY (booking_id) REFERENCES bookings(id)
);
### Add default users:
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'Admin');
INSERT INTO users (username, password, role) VALUES ('employee1', 'emp123', 'Employee');

### Running the Application
Clone the repo: git clone https://github.com/mohauEmma-Cyrpto/VehicleRentalApp.git
Navigate to the directory: cd VehicleRentalApp
Build the project: mvn clean install
Run it: mvn javafx:run

### Usage

**Admin**: Log in with admin / admin123 for full access.
**Employee**: Log in with employee1 / emp123 for restricted access.

### Features:
**Manage vehicles**(add/update/delete).
**Handle customer data.**
**Create and track bookings.**
**Process payments.**

### Project Structure

**DatabaseSetup.java:
Database connection and setup.
LoginController.java: User login and authentication
MainController.java: Main window and navigation.
UserManager.java: User operations.
VehicleController.java: Vehicle management.
CustomerController.java: Customer management.
BookingController.java: Booking operations.
PaymentController.java: Payment handling.**