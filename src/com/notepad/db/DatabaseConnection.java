package com.notepad.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials and URL. Adjust according to your local setup.
    private static final String URL = "jdbc:mysql://localhost:3306/notepad_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password"; // Set your DB password here

    private static Connection connection;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Ensure the driver is loaded (optional in newer JDBC, but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection successful.");
            } catch (SQLException | ClassNotFoundException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
