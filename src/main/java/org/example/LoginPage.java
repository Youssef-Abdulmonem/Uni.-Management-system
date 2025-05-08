package org.example;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Objects;

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
                switch (authenticate(id, password)) {
                    case 1:
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        frame.dispose();
                        new Student(id);
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        frame.dispose();
                        new Faculty(id);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid ID or Password.");
                        break;
                }
            }
        });
        frame.setVisible(true);
    }

    private int authenticate(String id, String pass) {
        try {
            String url = "jdbc:sqlite:database.db";
            Connection conn = DriverManager.getConnection(url);

            String sql = "SELECT id, password FROM students";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String studentId = rs.getString("id");
                String password = rs.getString("password");
                if(Objects.equals(id, studentId) && Objects.equals(password, pass) )
                    return 1;
            }
            sql = "SELECT id, password FROM faculties";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String studentId = rs.getString("id");
                String password = rs.getString("password");
                if(Objects.equals(id, studentId) && Objects.equals(password, pass) )
                    return 2;
            }
            rs.close();
            stmt.close();
            conn.close();
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
