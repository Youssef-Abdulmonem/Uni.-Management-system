package org.example;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class Student extends User {
    JFrame frame;

    String password, name, contact, email, admissionDate, academicStatus;

    public Student(String id) {
        super(id);

        frame = Frame.basicFrame("Student Page", 800, 700, true);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT name, password, contact, email ,admissionDate , academicStatus, FROM students WHERE id='" + id + "'";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                name = rs.getString("name");
                password = rs.getString("password");
                contact = rs.getString("contact");
                email = rs.getString("email");
                admissionDate = rs.getString("admissionDate");
                academicStatus = rs.getString("academicStatus");
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


        JButton registerButton = new JButton("Register for Course");
        JButton dropButton = new JButton("Drop Course");
        JButton viewGradesButton = new JButton("View Grades");
        JButton calculateGPAButton = new JButton("Calculate GPA");

        registerButton.setBounds(50, 100, 200, 30);
        dropButton.setBounds(50, 150, 200, 30);
        viewGradesButton.setBounds(50, 200, 200, 30);
        calculateGPAButton.setBounds(50, 250, 200, 30);

        frame.add(registerButton);
        frame.add(dropButton);
        frame.add(viewGradesButton);
        frame.add(calculateGPAButton);


        logout(frame);

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email);
        frame.add(updateProfileButton);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerForCourse(id);
            }
        });

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dropCourse(id);
            }
        });

        viewGradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewGrades(id);
            }
        });

        calculateGPAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateGPA(id);
            }
        });


        frame.setVisible(true);
    }


    public void registerForCourse(String id) {
        frame = Frame.basicFrame("Register for Courses", 600, 600, false);

        JLabel titleLabel = new JLabel("Select Courses to Register:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        java.util.List<JCheckBox> checkboxes = new java.util.ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();

            String query = "SELECT c.id, c.course_name " +
                    "FROM courses c " +
                    "WHERE c.id NOT IN (SELECT course_id FROM student_courses WHERE student_id = '" + id + "') " +
                    "AND (" +
                    "c.id NOT IN (SELECT course_id FROM course_prerequisites) " +
                    "OR EXISTS (" +
                    "SELECT 1 FROM course_prerequisites cp " +
                    "JOIN student_courses sc ON cp.prerequisite_id = sc.course_id " +
                    "WHERE cp.course_id = c.id " +
                    "AND sc.student_id = '" + id + "' " +
                    "AND sc.grade >= 50" +
                    ")" +
                    ")";

            ResultSet rs = stmt.executeQuery(query);

            int yPosition = 60;
            while (rs.next()) {
                String courseId = rs.getString("id");
                String courseName = rs.getString("course_name");

                JCheckBox courseCheckBox = new JCheckBox(courseId + " - " + courseName);
                courseCheckBox.setBounds(20, yPosition, 400, 25);
                checkboxes.add(courseCheckBox);
                frame.add(courseCheckBox);

                yPosition += 30;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 400, 200, 30);
        saveButton.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stmt = conn.createStatement();

                for (JCheckBox checkBox : checkboxes) {
                    if (checkBox.isSelected()) {
                        String[] parts = checkBox.getText().split(" - ");
                        String courseId = parts[0];
                        String insertQuery = "INSERT INTO student_courses (student_id, course_id, status) VALUES ('" + id + "', '" + courseId + "', 'Registered')";
                        stmt.executeUpdate(insertQuery);
                    }
                }

                stmt.close();
                conn.close();
                JOptionPane.showMessageDialog(null, "Courses registered successfully!");
                frame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error registering courses.");
            }
        });

        frame.add(saveButton);
        frame.setVisible(true);
    }


    public void dropCourse(String id) {
        frame = Frame.basicFrame("Drop Courses", 600, 600, false);

        JLabel titleLabel = new JLabel("Select Courses to Drop:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        java.util.List<JCheckBox> checkboxes = new java.util.ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.id, c.course_name " +
                            "FROM courses c " +
                            "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                            "WHERE sc.student_id = '" + id + "' AND sc.status = 'Registered'"
            );

            int yPosition = 60;
            while (rs.next()) {
                String courseId = rs.getString("id");
                String courseName = rs.getString("course_name");

                JCheckBox courseCheckBox = new JCheckBox(courseId + " - " + courseName, true);
                courseCheckBox.setBounds(20, yPosition, 400, 25);
                checkboxes.add(courseCheckBox);
                frame.add(courseCheckBox);

                yPosition += 30;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 400, 200, 30);
        saveButton.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stmt = conn.createStatement();

                for (JCheckBox checkBox : checkboxes) {
                    if (!checkBox.isSelected()) {
                        String[] parts = checkBox.getText().split(" - ");
                        String courseId = parts[0];
                        String deleteQuery = "DELETE FROM student_courses WHERE student_id='" + id + "' AND course_id='" + courseId + "'";
                        stmt.executeUpdate(deleteQuery);
                    }
                }

                stmt.close();
                conn.close();
                JOptionPane.showMessageDialog(null, "Courses dropped successfully!");
                frame.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error dropping courses.");
            }
        });

        frame.add(saveButton);
        frame.setVisible(true);
    }


    public void viewGrades(String id) {
        frame = Frame.basicFrame("View Grades", 650, 600, false);

        JLabel titleLabel = new JLabel("Your Registered Courses and Grades:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT c.id, c.course_name, c.description, c.credit_hours, c.schedule, sc.grade, sc.status " +
                    "FROM courses c " +
                    "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = '" + id + "'";
            ResultSet rs = stmt.executeQuery(query);

            int yPosition = 60;
            while (rs.next()) {
                String courseId = rs.getString("id");
                String courseName = rs.getString("course_name");
                String description = rs.getString("description");
                String creditHours = rs.getString("credit_hours");
                String schedule = rs.getString("schedule");
                String grade = rs.getString("grade");
                String status = rs.getString("status");
                double grd = rs.getDouble("grade");
                String courseInfo = "ID: " + courseId + ", Title: " + courseName + ", Credits: " + creditHours +
                        ", Schedule: " + schedule + ", Grade: " + grade + " (" + convertToGrade(grd) + ")" +
                        ", Status: " + status ;

                JLabel courseLabel = new JLabel(courseInfo);
                courseLabel.setBounds(20, yPosition, 600, 25);
                frame.add(courseLabel);

                yPosition += 30;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }


    public void calculateGPA(String id) {
        frame = Frame.basicFrame("Your GPA", 400, 200, false);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT c.credit_hours, sc.grade " +
                    "FROM courses c " +
                    "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = '" + id + "'";
            ResultSet rs = stmt.executeQuery(query);

            double totalPoints = 0.0;
            int totalCredits = 0;
            while (rs.next()) {
                if (rs.getDouble("grade") == 0.0)
                    continue;
                totalPoints += convertToPoints(rs.getDouble("grade")) * rs.getInt("credit_hours");
                totalCredits += rs.getInt("credit_hours");
            }

            double gpa = 0.0;

            if (totalCredits > 0)
                gpa = totalPoints / totalCredits;
            else
                gpa = 0.0;

            JLabel gpaLabel = new JLabel("Yours CGPA = " + String.format("%.2f", gpa));
            gpaLabel.setBounds(20, 60, 200, 25);
            frame.add(gpaLabel);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    private String convertToGrade(double x) {
        if (x >= 90)
            return "A";
        else if (x >= 85)
            return "A-";
        else if (x >= 80)
            return "B+";
        else if (x >= 75)
            return "B";
        else if (x >= 70)
            return "B-";
        else if (x >= 65)
            return "C+";
        else if (x >= 60)
            return "C";
        else if (x >= 56)
            return "C-";
        else if (x >= 53)
            return "D+";
        else if (x >= 50)
            return "D";
        else
            return "F";
    }

    private double convertToPoints(double x) {
        if (x >= 90)
            return 4.000;
        else if (x >= 85)
            return 3.666;
        else if (x >= 80)
            return 3.333;
        else if (x >= 75)
            return 3.000;
        else if (x >= 70)
            return 2.666;
        else if (x >= 65)
            return 2.333;
        else if (x >= 60)
            return 2.000;
        else if (x >= 56)
            return 1.666;
        else if (x >= 53)
            return 1.333;
        else if (x >= 50)
            return 1.000;
        else
            return 0.000;
    }


}