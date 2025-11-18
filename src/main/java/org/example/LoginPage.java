package org.example;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
    private JFrame frame;
    private JTextField idField;
    private JPasswordField passField;
    private JButton loginButton;

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
                    if (auth == 1) {
                        try {
                            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                            Statement stmt = conn.createStatement();
                            String sql = "SELECT allow_login FROM system_permissions WHERE id = 1";
                            ResultSet rs = stmt.executeQuery(sql);

                            if (rs.next()) {
                                int allowLogin = rs.getInt("allow_login");
                                if (allowLogin == 1) {
                                    frame.dispose();
                                    JOptionPane.showMessageDialog(null, "Login Successful! Welcome to University Management System.");
                                    new Student(id);
                                } else {
                                    JOptionPane.showMessageDialog(null, "You are not allowed to login.");
                                }
                            }
                            rs.close();
                            stmt.close();
                            conn.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Database error occurred.");
                        }
                    } else if (auth == 2) {
                        JOptionPane.showMessageDialog(null, "Login Successful! Welcome to University Management System.");
                        frame.dispose();
                        new Faculty(id);
                    } else if (auth == 3) {
                        JOptionPane.showMessageDialog(null, "Login Successful! Welcome to University Management System.");
                        frame.dispose();
                        new AdminStaff(id);
                    } else if (auth == 4) {
                        JOptionPane.showMessageDialog(null, "Login Successful! Welcome to University Management System.");
                        frame.dispose();
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
        String[] tables = {"students", "faculties", "adminstaff", "systemAdmin"};

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

            for (int i = 0; i < tables.length; i++) {

                String query = "SELECT 1 FROM " + tables[i] + " WHERE id = ? AND password = ? LIMIT 1";

                try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setString(1, id);
                    pstmt.setString(2, pass);

                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            // Return code: 1=student, 2=faculty, 3=adminstaff, 4=systemAdmin
                            return i + 1;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    private void forgetPassword(JFrame frame) {
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
                            try {
                                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                                Statement stmt = conn.createStatement();
                                String sql = "SELECT allow_reset FROM system_permissions WHERE id = 1";
                                ResultSet rs = stmt.executeQuery(sql);

                                if (rs.next()) {
                                    int allowReset = rs.getInt("allow_reset");
                                    if (allowReset == 1) {
                                        resetPassword(id, "students");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Password reset is not allowed at this time.");
                                    }
                                }

                                rs.close();
                                stmt.close();
                                conn.close();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Database error occurred.");
                            }
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
        String url = "jdbc:sqlite:database.db";

        String[] tables = {"students", "faculties", "adminstaff", "systemAdmin"};

        for (int i = 0; i < tables.length; i++) {
            String query = "SELECT * FROM " + tables[i] + " WHERE id=? AND email=?";

            try (Connection conn = DriverManager.getConnection(url);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, id);
                pstmt.setString(2, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return i + 1; // 1=students, 2=faculties, etc.
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
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

            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(resetFrame, "Password must be at least 6 characters long!");
                return;
            }

            String query = "UPDATE " + accountType + " SET password = ? WHERE id = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {
                PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, newPassword);
                    pstmt.setString(2, id);

                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Password updated successfully!");
                    resetFrame.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to update password. Try again later.");
            }
        });


        resetFrame.setVisible(true);
    }

}