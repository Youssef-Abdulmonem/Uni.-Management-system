package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SystemAdmin extends User {
    private JFrame frame;

    private String password, name, contact, email, securityLevel;

    public SystemAdmin(String id) {
        super(id);

        frame = Frame.basicFrame("System Admin Page", 800, 700, true);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT name, password, contact, email, securityLevel FROM systemAdmin WHERE id='" + id + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

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
                backupData();
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

    private void modifySystemSettings(String id) {
        JFrame frame = Frame.basicFrame("Manage Permissions", 400, 300, false);

        JCheckBox allowLogin = new JCheckBox("Allow Login");
        JCheckBox allowPassReset = new JCheckBox("Allow Password Resetting");
        JCheckBox allowUpdatingProfile = new JCheckBox("Allow Updating Profile");

        allowLogin.setBounds(50, 30, 300, 30);
        allowPassReset.setBounds(50, 70, 300, 30);
        allowUpdatingProfile.setBounds(50, 110, 300, 30);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 170, 100, 30);

        frame.add(allowLogin);
        frame.add(allowPassReset);
        frame.add(allowUpdatingProfile);
        frame.add(saveButton);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement()) {

            String query = "SELECT * FROM system_permissions WHERE id = 1";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                allowLogin.setSelected(rs.getInt("allow_login") == 1);
                allowPassReset.setSelected(rs.getInt("allow_reset") == 1);
                allowUpdatingProfile.setSelected(rs.getInt("allow_updating_profile") == 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saveButton.addActionListener(e -> {
            int drop = allowLogin.isSelected() ? 1 : 0;
            int register = allowPassReset.isSelected() ? 1 : 0;
            int update = allowUpdatingProfile.isSelected() ? 1 : 0;

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                 Statement stmt = conn.createStatement()) {

                String updateQuery = "UPDATE system_permissions SET " +
                        "allow_login = " + drop + ", " +
                        "allow_reset = " + register + ", " +
                        "allow_updating_profile = " + update +
                        " WHERE id = 1";

                stmt.executeUpdate(updateQuery);
                stmt.close();
                JOptionPane.showMessageDialog(frame, "Permissions updated successfully.");
                frame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    private void backupData() {
        JFrame backupFrame = Frame.basicFrame("Backup Data", 800, 600, false);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 20, 740, 480);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20, 20, 740, 480);
        backupFrame.add(scrollPane);

        JButton downloadButton = new JButton("Download");
        downloadButton.setBounds(350, 520, 100, 30);
        backupFrame.add(downloadButton);

        StringBuilder allData = new StringBuilder();

        String[] tables = {"systemAdmin", "adminStaff", "faculties", "students"};

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();

            for (String table : tables) {
                allData.append("=== ").append(table.toUpperCase()).append(" ===\n");

                ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        allData.append(meta.getColumnName(i)).append(": ").append(rs.getString(i)).append(" | ");
                    }
                    allData.append("\n");
                }
                allData.append("\n");
                rs.close();
            }

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data.");
        }

        textArea.setText(allData.toString());

        downloadButton.addActionListener(e -> {
            try {
                String fileName = "backup_data.txt";
                java.io.FileWriter writer = new java.io.FileWriter(fileName);
                writer.write(allData.toString());
                writer.close();
                JOptionPane.showMessageDialog(null, "Backup saved to " + fileName);
                backupFrame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving file.");
            }
        });

        backupFrame.setVisible(true);
    }

    private void managePermissions(String id)   {
        JFrame frame = Frame.basicFrame("Manage Permissions", 400, 300, false);

        JCheckBox allowLogin = new JCheckBox("Allow Dropping");
        JCheckBox allowPassReset = new JCheckBox("Allow Registering");

        allowLogin.setBounds(50, 30, 300, 30);
        allowPassReset.setBounds(50, 70, 300, 30);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(150, 170, 100, 30);

        frame.add(allowLogin);
        frame.add(allowPassReset);
        frame.add(saveButton);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement()) {

            String query = "SELECT * FROM system_permissions WHERE id = 1";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                allowLogin.setSelected(rs.getInt("allow_dropping") == 1);
                allowPassReset.setSelected(rs.getInt("allow_registering") == 1);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        saveButton.addActionListener(e -> {
            int drop = allowLogin.isSelected() ? 1 : 0;
            int register = allowPassReset.isSelected() ? 1 : 0;

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                 Statement stmt = conn.createStatement()) {

                String updateQuery = "UPDATE system_permissions SET " +
                        "allow_dropping = " + drop + ", " +
                        "allow_registering = " + register +
                        " WHERE id = 1";

                stmt.executeUpdate(updateQuery);
                stmt.close();
                JOptionPane.showMessageDialog(frame, "Permissions updated successfully.");
                frame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
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
                labels = new String[]{"name", "password", "contact", "email", "expertise"};
                prefix = "F";
                break;
            case "students":
                labels = new String[]{"name", "password", "contact", "email", "admissionDate", "academicStatus", "faculty", "department"};
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

        saveButton.addActionListener(e -> {
            if (validateFields(labels, fields)) {
                saveUserToDatabaseInsecure(userType, newId, labels, fields, formFrame);
            }
        });

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

    private boolean validateFields(String[] labels, JTextField[] fields) {
        for (int i = 0; i < labels.length; i++) {
            String label = labels[i];
            String value = fields[i].getText().trim();

            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(null, label + " can not be empty!");
                return false;
            }

            switch (label.toLowerCase()) {
                case "email":
                    if (value.startsWith("@") || (!value.endsWith("@gmail.com") && !value.contains("@yahoo.com") && !value.contains("@alexu.edu"))) {
                        JOptionPane.showMessageDialog(null, "Email must be a valid gmail, yahoo or alexu.edu email!");
                        return false;
                    }
                    break;

                case "contact":
                    if(value.length() != 11 || (!value.startsWith("010") && !value.startsWith("011") && !value.startsWith("012") && !value.startsWith("015"))) {
                        JOptionPane.showMessageDialog(null, "Contact number must be 11 digits long and starts with a valid prefix (010, 011, 012, 015)!");
                        return false;
                    }
                    break;

                case "password":
                    if (value.length() < 6) {
                        JOptionPane.showMessageDialog(null, "Password must be at least 6 characters long!");
                        return false;
                    }
                    break;

                case "securitylevel":
                    if (!value.equalsIgnoreCase("Low") && !value.equalsIgnoreCase("Medium") && !value.equalsIgnoreCase("High")) {
                        JOptionPane.showMessageDialog(null, "Security Level must be Low, Medium, or High!");
                        return false;
                    }
                    break;

                case "faculty":
                    if (!valueExistsInDatabase("faculties", "name", value)) {
                        JOptionPane.showMessageDialog(null, "Faculty '" + value + "' does not exist in the database.");
                        return false;
                    }
                    break;

                case "department":
                    if (!valueExistsInDatabase("departments", "name", value)) {
                        JOptionPane.showMessageDialog(null, "Department '" + value + "' does not exist in the database.");
                        return false;
                    }
                    break;

                case "role":
                    if(!value.equalsIgnoreCase("TA") && !value.equalsIgnoreCase("Doctor"))
                    {
                        JOptionPane.showMessageDialog(null, "Role must be TA or Doctor!");
                        return false;
                    }
                    break;

                case "officehours":
                    int officeHours = Integer.parseInt(value);
                    if (officeHours < 0 || officeHours > 72) {
                        JOptionPane.showMessageDialog(null, "Office hours must be between 0 and 72!");
                        return false;
                    }
                    break;

                case "admissiondate":
                    if (!value.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                        JOptionPane.showMessageDialog(null, "Admission Date must be in DD/MM/YYYY format.");
                        return false;
                    }

                    try {
                        String[] parts = value.split("/");
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int year = Integer.parseInt(parts[2]);

                        if (month < 1 || month > 12) {
                            throw new Exception("Invalid month");
                        }

                        int[] daysInMonth = {31, ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                        int maxDay = daysInMonth[month - 1];

                        if (day < 1 || day > maxDay) {
                            throw new Exception("Invalid day for the given month and year");
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Invalid admission date.");
                        return false;
                    }
                    break;


                case "academicstatus":
                    if (!value.equalsIgnoreCase("Graduated") && !value.equalsIgnoreCase("On Probation") && !value.equalsIgnoreCase("Active")) {
                        JOptionPane.showMessageDialog(null, "Academic Status must be Active or Graduated or On Probation.");
                        return false;
                    }
                    break;

                default:
                    break;
            }
        }
        return true;
    }

    private boolean valueExistsInDatabase(String tableName, String columnName, String value) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ? LIMIT 1")) {

            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            return exists;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}