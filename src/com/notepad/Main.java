package com.notepad;

import com.notepad.db.DatabaseConnection;
import com.notepad.model.User;
import com.notepad.ui.LoginDialog;
import com.notepad.ui.MainEditorFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Explicitly set the UI to look more native
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Verify database connection first
            System.out.println("Initializing Application...");
            if (DatabaseConnection.getConnection() == null) {
                JOptionPane.showMessageDialog(null, 
                    "Could not connect to the database. Running in offline mode without cloud features.", 
                    "Database Error", 
                    JOptionPane.WARNING_MESSAGE);
                // Launch without DB features
                MainEditorFrame editor = new MainEditorFrame(null);
                editor.setVisible(true);
                return;
            }

            // Launch Login Dialog
            JFrame dummyFrame = new JFrame();
            LoginDialog loginDialog = new LoginDialog(dummyFrame);
            loginDialog.setVisible(true);

            User loggedInUser = loginDialog.getLoggedInUser();
            
            if (loggedInUser != null) {
                // User logged in successfully
                MainEditorFrame editor = new MainEditorFrame(loggedInUser);
                editor.setVisible(true);
            } else {
                // User cancelled or closed the login dialog
                System.out.println("Login cancelled. Exiting...");
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });
        
        // Add a shutdown hook to cleanly close DB connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down Application...");
            DatabaseConnection.closeConnection();
        }));
    }
}
