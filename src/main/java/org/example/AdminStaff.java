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

    }

    private void createCourse(String id) {

    }

    private void assignFaculty(String id) {

    }

    private void generateReports(String id) {

    }

}
