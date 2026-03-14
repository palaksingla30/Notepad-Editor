package com.notepad.dao;

import com.notepad.db.DatabaseConnection;
import com.notepad.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    // Basic password checking (Note: in a real app, hash and salt passwords!)
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String username, String password) {
        String queryCheck = "SELECT user_id FROM Users WHERE username = ?";
        String insertSql = "INSERT INTO Users (username, password) VALUES (?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try {
            
            // Check if username already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(queryCheck)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Username already exists.");
                    return false;
                }
            }

            // Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                int rowsAffected = insertStmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
