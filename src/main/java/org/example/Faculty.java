package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.server.ExportException;

import java.sql.*;

public class Faculty extends User {
    JFrame frame;
    String password, name, contact, email, expertise;

    public Faculty(String id) {
        super(id);
        frame = Frame.basicFrame("Faculty Page", 800, 700, true);
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT name, password, contact, email , expertise , FROM faculties WHERE id='" + id + "'";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                name = rs.getString("name");
                password = rs.getString("password");
                contact = rs.getString("contact");
                email = rs.getString("email");
                expertise = rs.getString("expertise");
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
        JButton assignGrades = new JButton("Assign Grades");
        JButton manageCourses = new JButton("Manage Courses");
        JButton setOfficeHours = new JButton("Set Office Hours");
        JButton generateReports = new JButton("Generate Reports");
        assignGrades.setBounds(50, 100, 200, 30);
        manageCourses.setBounds(50, 150, 200, 30);
        setOfficeHours.setBounds(50, 200, 200, 30);
        generateReports.setBounds(50, 250, 200, 30);

        frame.add(assignGrades);
        frame.add(manageCourses);
        frame.add(setOfficeHours);
        frame.add(generateReports);

        logout(frame);

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email);
        frame.add(updateProfileButton);


        assignGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });

        manageCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });

        setOfficeHours.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });

        generateReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //
            }
        });


        frame.setVisible(true);

    }
    private void assignGrades(JFrame frame) {

    }
}
