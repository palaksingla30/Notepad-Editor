package com.notepad.ui;

import com.notepad.dao.UserDAO;
import com.notepad.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton cancelButton;
    private User loggedInUser;
    private UserDAO userDAO;

    public LoginDialog(JFrame parent) {
        super(parent, "Login to Notepad Editor", true);
        userDAO = new UserDAO();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);

        JLabel labelUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(labelUsername, cs);

        usernameField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(usernameField, cs);

        JLabel labelPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(labelPassword, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        JPanel bp = new JPanel();
        bp.add(loginButton);
        bp.add(registerButton);
        bp.add(cancelButton);

        // Styling buttons nicely
        loginButton.setBackground(new Color(60, 140, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        registerButton.setBackground(new Color(100, 180, 100));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(true);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Please enter both username and password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User user = userDAO.loginUser(username, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Login successful. Welcome " + user.getUsername() + "!",
                            "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    loggedInUser = user;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // Reset fields
                    usernameField.setText("");
                    passwordField.setText("");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 RegisterDialog dialog = new RegisterDialog(parent, userDAO);
                 dialog.setVisible(true);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
