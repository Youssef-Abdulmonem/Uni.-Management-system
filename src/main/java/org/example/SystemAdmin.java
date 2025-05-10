package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SystemAdmin extends User {
    JFrame frame;

    String password, name, contact, email, securityLevel;

    public SystemAdmin(String id) {
        super(id);

        frame = Frame.basicFrame("System Admin Page", 800, 700, true);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT name, password, contact, email, securityLevel FROM systemAdmin WHERE id='" + id + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println(id);

            if (rs.next()) {
                name = rs.getString("name");
                password = rs.getString("password");
                contact = rs.getString("contact");
                email = rs.getString("email");
                securityLevel = rs.getString("securityLevel");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel helloMessage = new JLabel("Hello " + name + ", Welcome to University Management System!");
        helloMessage.setBounds(50, 50, 400, 30);
        frame.add(helloMessage);

        JButton createUser = new JButton("Create User");
        JButton modifySystemSettings = new JButton("Modify System Settings");
        JButton backupData = new JButton("Backup Data");
        JButton managePermissions = new JButton("Manage Permissions");

        createUser.setBounds(50, 100, 200, 30);
        modifySystemSettings.setBounds(50, 150, 200, 30);
        backupData.setBounds(50, 200, 200, 30);
        managePermissions.setBounds(50, 250, 200, 30);

        frame.add(createUser);
        frame.add(modifySystemSettings);
        frame.add(backupData);
        frame.add(managePermissions);


        logout(frame);

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email, "systemAdmin");
        frame.add(updateProfileButton);

        createUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });

        modifySystemSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifySystemSettings(id);
            }
        });

        backupData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backupData(id);
            }
        });

        managePermissions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                managePermissions(id);
            }
        });

        frame.setVisible(true);
    }




    private void createUser() {
        JFrame userTypeFrame = Frame.basicFrame("Create a New User", 400, 300, false);

        JLabel label = new JLabel("Create a New User");
        label.setBounds(120, 20, 200, 30);
        userTypeFrame.add(label);

        JButton saButton = new JButton("System Admin");
        JButton asButton = new JButton("Admin Staff");
        JButton facButton = new JButton("Faculties");
        JButton stuButton = new JButton("Students");

        saButton.setBounds(50, 70, 300, 30);
        asButton.setBounds(50, 110, 300, 30);
        facButton.setBounds(50, 150, 300, 30);
        stuButton.setBounds(50, 190, 300, 30);

        userTypeFrame.add(saButton);
        userTypeFrame.add(asButton);
        userTypeFrame.add(facButton);
        userTypeFrame.add(stuButton);

        saButton.addActionListener(e -> openUserForm("systemAdmin"));
        asButton.addActionListener(e -> openUserForm("adminStaff"));
        facButton.addActionListener(e -> openUserForm("faculties"));
        stuButton.addActionListener(e -> openUserForm("students"));

        userTypeFrame.setVisible(true);
    }

    private void openUserForm(String userType) {
        JFrame formFrame = Frame.basicFrame("Create " + userType, 400, 500, false);

        String[] labels;
        String prefix;

        switch (userType) {
            case "systemAdmin":
                labels = new String[]{"name", "password", "contact", "email", "securityLevel"};
                prefix = "SA";
                break;
            case "adminStaff":
                labels = new String[]{"name", "password", "contact", "email", "faculty", "department", "role", "officeHours"};
                prefix = "S";
                break;
            case "faculties":
                labels = new String[]{"name", "password", "email", "contact", "expertise"};
                prefix = "F";
                break;
            case "students":
                labels = new String[]{"name", "password", "email", "contact", "admissionDate", "academicStatus", "faculty", "department"};
                prefix = "st";
                break;
            default:
                return;
        }

        JTextField[] fields = new JTextField[labels.length];
        int y = 20;
        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            l.setBounds(50, y, 100, 25);
            formFrame.add(l);

            JTextField f = new JTextField();
            f.setBounds(160, y, 150, 25);
            formFrame.add(f);
            fields[i] = f;

            y += 40;
        }

        String newId = generateNewId(userType, prefix);
        JLabel idLabel = new JLabel("ID: " + newId);
        idLabel.setBounds(50, y, 200, 25);
        formFrame.add(idLabel);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, y + 40, 100, 30);
        formFrame.add(saveButton);

        saveButton.addActionListener(e -> saveUserToDatabaseInsecure(userType, newId, labels, fields, formFrame));

        formFrame.setVisible(true);
    }

    private String generateNewId(String table, String prefix) {
        String newId = "";
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            String query = "SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            int num = 0;
            if (rs.next()) {
                String lastId = rs.getString("id").replaceAll("\\D+", "");
                num = Integer.parseInt(lastId);
            }
            num++;
            newId = prefix + String.format("%02d", num);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    private void saveUserToDatabaseInsecure(String table, String id, String[] labels, JTextField[] fields, JFrame frame) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            StringBuilder cols = new StringBuilder("id");
            StringBuilder vals = new StringBuilder("'" + id + "'");
            for (int i = 0; i < labels.length; i++) {
                cols.append(", ").append(labels[i]);
                vals.append(", '").append(fields[i].getText()).append("'");
            }
            String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
            JOptionPane.showMessageDialog(null, "User created successfully!");

            frame.dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving user.");
        }
    }



    private void modifySystemSettings(String id) {

    }

    private void backupData(String id) {

    }

    private void managePermissions(String id) {

    }
}
