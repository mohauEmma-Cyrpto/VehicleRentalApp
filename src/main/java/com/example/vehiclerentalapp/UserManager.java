package com.example.vehiclerentalapp;

import java.sql.*;

public class UserManager {
    private final User loggedInUser;

    public UserManager(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public User getUserById(int userId) throws SQLException {
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, username, role FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String role = rs.getString("role");
                return new User(id, username, role);
            }
            return null;
        }
    }

    public void addUser(String username, String password, String role) throws SQLException {
        if (!loggedInUser.getRole().equals("Admin")) {
            throw new SecurityException("Only Admins can add users.");
        }
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
        }
    }

    public void updateUser(int userId, String username, String password, String role) throws SQLException {
        if (!loggedInUser.getRole().equals("Admin")) {
            throw new SecurityException("Only Admins can update users.");
        }
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        if (!loggedInUser.getRole().equals("Admin")) {
            throw new SecurityException("Only Admins can delete users.");
        }
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}