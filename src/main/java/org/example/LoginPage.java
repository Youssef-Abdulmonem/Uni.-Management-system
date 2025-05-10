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
        frame = Frame.basicFrame("Login Page", 350, 250, true);

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
        loginButton.setBounds(100, 110, 160, 25);
        frame.add(loginButton);

        forgetPassword(frame);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passField.getPassword());
                int auth = authenticate(id, password);
                if (auth > 0) {
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

    public void forgetPassword(JFrame frame) {
        JButton forgetButton = new JButton("Forget Password");
        forgetButton.setBounds(80, 150, 200, 25);
        frame.add(forgetButton);

        forgetButton.addActionListener(e -> {
            JFrame forgetFrame = Frame.basicFrame("Forget Password", 300, 200, false);

            JLabel idLabel = new JLabel("Enter ID:");
            idLabel.setBounds(20, 20, 80, 25);
            forgetFrame.add(idLabel);

            idField = new JTextField();
            idField.setBounds(100, 20, 160, 25);
            forgetFrame.add(idField);


            JLabel emailLabel = new JLabel("Enter Email:");
            emailLabel.setBounds(20, 60, 80, 25);
            forgetFrame.add(emailLabel);

            JTextField emailField = new JTextField();
            emailField.setBounds(100, 60, 160, 25);
            forgetFrame.add(emailField);


            JButton resetButton = new JButton("Reset Password");
            resetButton.setBounds(70, 100, 170, 25);
            forgetFrame.add(resetButton);


            resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String id = idField.getText();
                    String email = new String(emailField.getText());
                    int auth = resetAuthenticate(id, email);
                    if (auth > 0) {
                        forgetFrame.dispose();

                        if (auth == 1) {
                            resetPassword(id, "students");
                        } else if (auth == 2) {
                            resetPassword(id, "faculties");
                        } else if (auth == 3) {
                            resetPassword(id, "adminStaff");
                        } else if (auth == 4) {
                            resetPassword(id, "systemAdmin");
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid ID or Email.");
                    }
                }
            });


            forgetFrame.setVisible(true);
        });

    }

    private int resetAuthenticate(String id, String email) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();

            String studentQuery = "SELECT * FROM students WHERE id='" + id + "' AND email='" + email + "'";
            ResultSet studentRs = stmt.executeQuery(studentQuery);
            if (studentRs.next()) {
                studentRs.close();
                stmt.close();
                conn.close();
                return 1;
            }
            studentRs.close();


            String facultyQuery = "SELECT * FROM faculties WHERE id='" + id + "' AND email='" + email + "'";
            ResultSet facultyRs = stmt.executeQuery(facultyQuery);
            if (facultyRs.next()) {
                facultyRs.close();
                stmt.close();
                conn.close();
                return 2;
            }
            facultyRs.close();


            String adminQuery = "SELECT * FROM adminstaff WHERE id='" + id + "' AND email='" + email + "'";
            ResultSet adminRs = stmt.executeQuery(adminQuery);
            if (adminRs.next()) {
                adminRs.close();
                stmt.close();
                conn.close();
                return 3;
            }
            adminRs.close();


            String sysAdminQuery = "SELECT * FROM systemAdmin WHERE id='" + id + "' AND email='" + email + "'";
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

    private void resetPassword(String id, String accountType) {
        JFrame resetFrame = Frame.basicFrame("Reset Password", 300, 200, false);

        JLabel passLabel = new JLabel("New Password:");
        passLabel.setBounds(20, 40, 100, 25);
        resetFrame.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(130, 40, 120, 25);
        resetFrame.add(passField);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(90, 100, 100, 30);
        resetFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String newPassword = new String(passField.getPassword());

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stmt = conn.createStatement();
                String query = "UPDATE " + accountType + " SET password = '" + newPassword + "' WHERE id = '" + id + "'";

                stmt.executeUpdate(query);

                stmt.close();
                conn.close();

                JOptionPane.showMessageDialog(null, "Password updated successfully!");

                resetFrame.dispose();

                if (accountType.equals("students")) {
                    new Student(id);
                } else if (accountType.equals("faculties")) {
                    new Faculty(id);
                } else if (accountType.equals("adminStaff")) {
                    new AdminStaff(id);
                } else if (accountType.equals("systemAdmin")) {
                    new SystemAdmin(id);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to update password");
            }
        });

        resetFrame.setVisible(true);
    }


}