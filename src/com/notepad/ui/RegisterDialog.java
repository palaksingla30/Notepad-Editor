package com.notepad.ui;

import com.notepad.dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createButton;
    private JButton cancelButton;

    public RegisterDialog(JFrame parent, UserDAO userDAO) {
        super(parent, "Register New Account", true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);

        JLabel labelUsername = new JLabel("Username: ");
        cs.gridx = 0; cs.gridy = 0; cs.gridwidth = 1;
        panel.add(labelUsername, cs);

        usernameField = new JTextField(20);
        cs.gridx = 1; cs.gridy = 0; cs.gridwidth = 2;
        panel.add(usernameField, cs);

        JLabel labelPassword = new JLabel("Password: ");
        cs.gridx = 0; cs.gridy = 1; cs.gridwidth = 1;
        panel.add(labelPassword, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1; cs.gridy = 1; cs.gridwidth = 2;
        panel.add(passwordField, cs);

        JLabel labelConfirm = new JLabel("Confirm: ");
        cs.gridx = 0; cs.gridy = 2; cs.gridwidth = 1;
        panel.add(labelConfirm, cs);

        confirmPasswordField = new JPasswordField(20);
        cs.gridx = 1; cs.gridy = 2; cs.gridwidth = 2;
        panel.add(confirmPasswordField, cs);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createButton = new JButton("Create Account");
        cancelButton = new JButton("Cancel");
        
        createButton.setBackground(new Color(100, 180, 100));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setContentAreaFilled(false);
        createButton.setOpaque(true);

        JPanel bp = new JPanel();
        bp.add(createButton);
        bp.add(cancelButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword());
                String confirm = new String(confirmPasswordField.getPassword());

                if (username.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "All fields are required.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!pass.equals(confirm)) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Passwords do not match.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = userDAO.registerUser(username, pass);
                if (success) {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Account created successfully! You can now login.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(RegisterDialog.this,
                            "Registration failed. Username might already exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
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
}
