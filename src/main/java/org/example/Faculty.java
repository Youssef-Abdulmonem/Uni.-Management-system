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

            String query = "SELECT name, password, contact, email , expertise FROM faculties WHERE id='" + id + "'";

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
            public void actionPerformed(ActionEvent e) { assignGrades(id); }
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
    private void assignGrades(String id) {
        frame = Frame.basicFrame("Assign Grades", 800, 700, false);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement()) {

            String query = "SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + id + "'";
            ResultSet rs = stmt.executeQuery(query);

            int yPosition = 30;
            while (rs.next()) {
                String courseID = rs.getString("course_id");

                String courseNameQuery = "SELECT course_name FROM courses WHERE id = '" + courseID + "'";
                Statement courseStmt = conn.createStatement();
                ResultSet courseRs = courseStmt.executeQuery(courseNameQuery);

                String courseName;
                if (courseRs.next()) {
                    courseName = courseRs.getString("course_name");
                } else {
                    courseName = courseID;
                }

                JButton courseButton = new JButton(courseName);
                courseButton.setBounds(50, yPosition, 200, 30);
                yPosition += 40;

                String finalCourseID = courseID;

                courseButton.addActionListener(e -> {
                    JFrame gradesFrame = Frame.basicFrame("Assign grades for: " + finalCourseID + " - " + courseName, 400, 600, false);
                    gradesFrame.setLayout(null);

                    try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                         Statement stm = con.createStatement()) {

                        String studentQuery = "SELECT student_id FROM student_courses WHERE course_id = '" + finalCourseID + "'";
                        ResultSet studentRs = stm.executeQuery(studentQuery);

                        int yPos = 30;
                        java.util.List<JTextField> gradeFields = new java.util.ArrayList<>();
                        java.util.List<String> studentIds = new java.util.ArrayList<>();

                        while (studentRs.next()) {
                            String studentId = studentRs.getString("student_id");
                            studentIds.add(studentId);

                            String studentName = studentId;

                            String nameQuery = "SELECT name FROM students WHERE id = '" + studentId + "'";
                            try (Statement nameStmt = con.createStatement();
                                 ResultSet nameRs = nameStmt.executeQuery(nameQuery)) {
                                if (nameRs.next()) {
                                    studentName = nameRs.getString("name");
                                }
                            }

                            JLabel studentLabel = new JLabel("Student Name: : " + studentName + "ID: " + studentId);
                            studentLabel.setBounds(30, yPos, 250, 25);
                            gradesFrame.add(studentLabel);

                            JTextField gradeField = new JTextField();
                            gradeField.setBounds(280, yPos, 80, 25);
                            gradesFrame.add(gradeField);

                            gradeFields.add(gradeField);
                            yPos += 40;
                        }

                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(150, yPos, 100, 30);
                        gradesFrame.add(saveButton);

                        saveButton.addActionListener(ev -> {
                            try (Connection saveConn = DriverManager.getConnection("jdbc:sqlite:database.db")) {
                                for (int i = 0; i < studentIds.size(); i++) {
                                    String studentId = studentIds.get(i);
                                    String grade = gradeFields.get(i).getText();

                                    String updateQuery = "UPDATE student_courses SET grade = '" + grade + "' WHERE student_id = '" + studentId + "' AND course_id = '" + finalCourseID + "'";
                                    try (Statement updateStmt = saveConn.createStatement()) {
                                        updateStmt.executeUpdate(updateQuery);
                                    }
                                }
                                JOptionPane.showMessageDialog(gradesFrame, "Grades saved successfully!");
                                gradesFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });

                        gradesFrame.setVisible(true);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                frame.add(courseButton);

                courseRs.close();
                courseStmt.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

}
