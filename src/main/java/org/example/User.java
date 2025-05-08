package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public abstract class User {
    public String id;
    JFrame frame;

    public User(String id) {
        this.id = id;
    }

    public static void logout(JFrame frame) {
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(650, 20, 100, 30);
        frame.add(logoutButton);

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage();
        });
    }

    public JButton updateProfile(JFrame frame, String id, String password, String contact, String email) {
        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.setBounds(600, 60, 150, 30);
        frame.add(updateProfileButton);

        updateProfileButton.addActionListener(e -> {
            JFrame updateFrame;
            updateFrame = Frame.basicFrame("Update Profile", 400, 400, false);

            JButton updatePasswordButton = new JButton("Update Password");
            JButton updateContactButton = new JButton("Update Contact");
            JButton updateEmailButton = new JButton("Update Email");

            updatePasswordButton.setBounds(50, 50, 150, 30);
            updateContactButton.setBounds(50, 100, 150, 30);
            updateEmailButton.setBounds(50, 150, 150, 30);

            updateFrame.add(updatePasswordButton);
            updateFrame.add(updateContactButton);
            updateFrame.add(updateEmailButton);

            updatePasswordButton.addActionListener(ev -> openUpdateFrame("password", id));
            updateContactButton.addActionListener(ev -> openUpdateFrame("contact", id));
            updateEmailButton.addActionListener(ev -> openUpdateFrame("email", id));

            updateFrame.setVisible(true);
        });

        return updateProfileButton;
    }

    private void openUpdateFrame(String field, String id) {
        JFrame updateFrame = Frame.basicFrame("Update " + field, 400, 200, false);

        String oldValue = getCurrentValue(field, id);

        JLabel oldLabel = new JLabel("Old " + field + ": " + oldValue);
        JLabel newLabel = new JLabel("New " + field + ":");
        JTextField newField = new JTextField();
        JButton saveButton = new JButton("Save");

        oldLabel.setBounds(30, 20, 300, 25);
        newLabel.setBounds(30, 60, 100, 25);
        newField.setBounds(140, 60, 200, 25);
        saveButton.setBounds(140, 100, 100, 30);

        updateFrame.add(oldLabel);
        updateFrame.add(newLabel);
        updateFrame.add(newField);
        updateFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String newValue = newField.getText();
            if (!newValue.isEmpty()) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                     Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("UPDATE students SET " + field + "='" + newValue + "' WHERE id='" + id + "'");
                    JOptionPane.showMessageDialog(updateFrame, field + " updated successfully!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(updateFrame, "Input is empty!");
            }
            updateFrame.dispose();
        });

        updateFrame.setVisible(true);
    }

    private String getCurrentValue(String field, String id) {
        String value = "";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + field + " FROM students WHERE id='" + id + "'")) {
            if (rs.next()) {
                value = rs.getString(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
