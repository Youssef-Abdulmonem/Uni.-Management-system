package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminStaff extends User {
    JFrame frame;

    String password, name, contact, email, role, officeHour, faculty, department;

    public AdminStaff(String id) {
        super(id);

        frame = Frame.basicFrame("Admin Staff Page", 800, 700, true);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT name, password, contact, email, faculty, department, role, officeHours FROM adminstaff WHERE id='" + id + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                name = rs.getString("name");
                password = rs.getString("password");
                contact = rs.getString("contact");
                email = rs.getString("email");
                officeHour = rs.getString("officeHour");
                role = rs.getString("role");
                faculty = rs.getString("faculty");
                department = rs.getString("department");
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

        JButton registerStudent = new JButton("Register Student");
        JButton createCourse = new JButton("Create Course");
        JButton assignFaculty = new JButton("Assign Faculty");
        JButton generateReports = new JButton("Generate Reports");

        registerStudent.setBounds(50, 100, 200, 30);
        createCourse.setBounds(50, 150, 200, 30);
        assignFaculty.setBounds(50, 200, 200, 30);
        generateReports.setBounds(50, 250, 200, 30);

        frame.add(registerStudent);
        frame.add(createCourse);
        frame.add(assignFaculty);
        frame.add(generateReports);


        logout(frame);

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email, "students");
        frame.add(updateProfileButton);

        registerStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStudent(id);
            }
        });

        createCourse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCourse(id);
            }
        });

        assignFaculty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignFaculty(id);
            }
        });

        generateReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReports(id);
            }
        });

        frame.setVisible(true);
    }

    private void registerStudent(String adminId) {
        JFrame registerFrame = Frame.basicFrame("Register New Student", 400, 500, true);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel passLabel = new JLabel("Password:");
        JTextField passField = new JTextField();

        JLabel contactLabel = new JLabel("Contact:");
        JTextField contactField = new JTextField();

        JLabel admissionDateLabel = new JLabel("Admission Date:");
        JTextField admissionDateField = new JTextField();

        JLabel academicStatusLabel = new JLabel("Academic Status:");
        JTextField academicStatusField = new JTextField();

        JLabel facultyLabel = new JLabel("Faculty:");
        JTextField facultyField = new JTextField();

        JLabel departmentLabel = new JLabel("Department:");
        JTextField departmentField = new JTextField();

        JButton saveButton = new JButton("Save");

        nameLabel.setBounds(30, 30, 120, 25);
        nameField.setBounds(150, 30, 200, 25);
        passLabel.setBounds(30, 70, 120, 25);
        passField.setBounds(150, 70, 200, 25);
        contactLabel.setBounds(30, 110, 120, 25);
        contactField.setBounds(150, 110, 200, 25);
        admissionDateLabel.setBounds(30, 150, 120, 25);
        admissionDateField.setBounds(150, 150, 200, 25);
        academicStatusLabel.setBounds(30, 190, 120, 25);
        academicStatusField.setBounds(150, 190, 200, 25);
        facultyLabel.setBounds(30, 230, 120, 25);
        facultyField.setBounds(150, 230, 200, 25);
        departmentLabel.setBounds(30, 270, 120, 25);
        departmentField.setBounds(150, 270, 200, 25);
        saveButton.setBounds(150, 320, 100, 30);

        registerFrame.add(nameLabel);
        registerFrame.add(nameField);
        registerFrame.add(passLabel);
        registerFrame.add(passField);
        registerFrame.add(contactLabel);
        registerFrame.add(contactField);
        registerFrame.add(admissionDateLabel);
        registerFrame.add(admissionDateField);
        registerFrame.add(academicStatusLabel);
        registerFrame.add(academicStatusField);
        registerFrame.add(facultyLabel);
        registerFrame.add(facultyField);
        registerFrame.add(departmentLabel);
        registerFrame.add(departmentField);
        registerFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String password = passField.getText();
            String contact = contactField.getText();
            String admissionDate = admissionDateField.getText();
            String academicStatus = academicStatusField.getText();
            String faculty = facultyField.getText();
            String department = departmentField.getText();

            int maxNumber = 0;
            String newId = "";

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id FROM students WHERE id LIKE 'st%'");

                while (rs.next()) {
                    String existingId = rs.getString("id");
                    int num = Integer.parseInt(existingId.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                }

                int newNumber = maxNumber + 1;
                newId = String.format("st%02d", newNumber);

                rs.close();
                stmt.close();
                conn.close();

                Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stm = con.createStatement();

                String insertQuery = "INSERT INTO students (id, name, password, contact, admissionDate, academicStatus, faculty, department) " +
                        "VALUES ('" + newId + "', '" + name + "', '" + password + "', '" + contact + "', '" + admissionDate + "', '" +
                        academicStatus + "', '" + faculty + "', '" + department + "')";

                stm.executeUpdate(insertQuery);

                JOptionPane.showMessageDialog(registerFrame, "Student registered with ID: " + newId);
                registerFrame.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registerFrame, "Failed to register student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerFrame.setVisible(true);
    }



    private void createCourse(String id) {

    }

    private void assignFaculty(String id) {

    }

    private void generateReports(String id) {

    }

}
