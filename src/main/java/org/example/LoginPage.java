package org.example;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
    JFrame frame;
    JTextField idField;
    JPasswordField passField;
    JButton loginButton;

    public LoginPage() {
        frame = Frame.basicFrame("Login Page", 300, 200, true);

        JLabel idLabel = new JLabel("Enter ID:");
        idLabel.setBounds(20, 20, 80, 25);
        frame.add(idLabel);

        idField = new JTextField();
        idField.setBounds(100, 20, 160, 25);
        frame.add(idField);

        JLabel passLabel = new JLabel("Enter Password:");
        passLabel.setBounds(20, 60, 120, 25);
        frame.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(140, 60, 120, 25);
        frame.add(passField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 100, 160, 25);
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passField.getPassword());
                int auth = authenticate(id, password);
                if (auth > 0) {
                    JOptionPane.showMessageDialog(null, "Login Successful! Welcome to University Management System.");
                    frame.dispose();
                    if (auth == 1) {
                        new Student(id);
                    } else if (auth == 2) {
                        new Faculty(id);
                    } else if (auth == 3) {
                        new AdminStaff(id);
                    } else if (auth == 4) {
                        new SystemAdmin(id);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid ID or Password.");
                }
            }
        });
        frame.setVisible(true);
    }

    private int authenticate(String id, String pass) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();

            String studentQuery = "SELECT * FROM students WHERE id='" + id + "' AND password='" + pass + "'";
            ResultSet studentRs = stmt.executeQuery(studentQuery);
            if (studentRs.next()) {
                studentRs.close();
                stmt.close();
                conn.close();
                return 1;
            }
            studentRs.close();


            String facultyQuery = "SELECT * FROM faculties WHERE id='" + id + "' AND password='" + pass + "'";
            ResultSet facultyRs = stmt.executeQuery(facultyQuery);
            if (facultyRs.next()) {
                facultyRs.close();
                stmt.close();
                conn.close();
                return 2;
            }
            facultyRs.close();


            String adminQuery = "SELECT * FROM adminstaff WHERE id='" + id + "' AND password='" + pass + "'";
            ResultSet adminRs = stmt.executeQuery(adminQuery);
            if (adminRs.next()) {
                adminRs.close();
                stmt.close();
                conn.close();
                return 3;
            }
            adminRs.close();


            String sysAdminQuery = "SELECT * FROM systemAdmin WHERE id='" + id + "' AND password='" + pass + "'";
            ResultSet sysAdminRs = stmt.executeQuery(sysAdminQuery);
            if (sysAdminRs.next()) {
                sysAdminRs.close();
                stmt.close();
                conn.close();
                return 4;
            }
            sysAdminRs.close();
            stmt.close();
            conn.close();


        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}