package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.server.ExportException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        JButton generateReports = new JButton("View Student Roster");

        assignGrades.setBounds(50, 100, 200, 30);
        manageCourses.setBounds(50, 150, 200, 30);
        setOfficeHours.setBounds(50, 200, 200, 30);
        generateReports.setBounds(50, 250, 200, 30);

        frame.add(assignGrades);
        frame.add(manageCourses);
        frame.add(setOfficeHours);
        frame.add(generateReports);

        logout(frame);

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email, "faculties");
        frame.add(updateProfileButton);

        assignGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignGrades(id);
            }
        });

        manageCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manageCourse(id);
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
                    JFrame gradesFrame = Frame.basicFrame("Assign grades for: " + finalCourseID + " - " + courseName,
                            400, 600, false);

                    try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                         Statement stm = con.createStatement()) {

                        String studentQuery = "SELECT student_id FROM student_courses WHERE course_id = '"
                                + finalCourseID + "'";
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

                            JLabel studentLabel = new JLabel("Name: " + studentName + ", ID: " + studentId);
                            studentLabel.setBounds(30, yPos, 250, 25);
                            gradesFrame.add(studentLabel);

                            String grade = "";
                            String gradeQuery = "SELECT grade FROM student_courses WHERE student_id = '" + studentId + "' AND course_id = '" + finalCourseID + "'";
                            try (Connection gradeConn = DriverManager.getConnection("jdbc:sqlite:database.db");
                                 Statement gradeStmt = gradeConn.createStatement();
                                 ResultSet gradeRs = gradeStmt.executeQuery(gradeQuery)) {
                                if (gradeRs.next()) {
                                    grade = gradeRs.getString("grade");
                                } else {
                                    grade = "N/A";
                                    System.out.println("No grade found for studentId: " + studentId + ", courseId: " + finalCourseID);
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }

                            JTextField gradeField = new JTextField(grade);
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
                                    String gradeText = gradeFields.get(i).getText();

                                    double grade;
                                    try {
                                        grade = Double.parseDouble(gradeText);
                                        if (grade < 0 || grade > 100) {
                                            throw new NumberFormatException();
                                        }
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(gradesFrame,
                                                "Invalid grade entered for student ID: " + studentId + ". Please enter a valid number.",
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    String updateQuery = "UPDATE student_courses SET grade = '" + grade
                                            + "' WHERE student_id = '" + studentId + "' AND course_id = '"
                                            + finalCourseID + "'";
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

    public static void manageCourse(String facultyId) {
        JFrame frame = Frame.basicFrame("Manage Courses", 800, 700, false);

        JButton changeName = new JButton("Change course name");
        changeName.setBounds(50, 100, 200, 30);
        frame.add(changeName);

        
        JButton creditHours = new JButton("Change Credit Hours Course");
        creditHours.setBounds(50, 150, 200, 30);
        frame.add(creditHours);
        
        

        changeName.addActionListener(ev -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Get unique course IDs
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + facultyId + "'");
                ArrayList<String> courseIds = new ArrayList<>();
                while (rs1.next()) {
                    courseIds.add(rs1.getString("course_id"));
                }
                rs1.close();
                stmt1.close();

                // Get course names
                HashMap<String, String> courseMap = new HashMap<>();
                Statement stmt2 = conn.createStatement();
                for (String courseId : courseIds) {
                    ResultSet rs2 = stmt2.executeQuery("SELECT course_name FROM courses WHERE id = '" + courseId + "'");
                    if (rs2.next()) {
                        courseMap.put(courseId, rs2.getString("course_name"));
                    }
                    rs2.close();
                }
                stmt2.close();

                // Create new frame for courses
                JFrame courseFrame = Frame.basicFrame("Courses in Faculty", 500, 500, false);

                int y = 20;
                for (String courseId : courseMap.keySet()) {
                    String courseName = courseMap.get(courseId);

                    JButton courseButton = new JButton(courseName);
                    courseButton.setBounds(50, y, 200, 30);
                    courseFrame.add(courseButton);
                    y += 50;

                    courseButton.addActionListener(e -> {
                        JFrame editFrame = Frame.basicFrame("Edit Course Name", 400, 200, false);

                        JLabel label = new JLabel("Course Name:");
                        label.setBounds(20, 20, 100, 25);
                        editFrame.add(label);

                        JTextField textField = new JTextField(courseName);
                        textField.setBounds(130, 20, 200, 25);
                        editFrame.add(textField);

                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(130, 60, 100, 30);
                        editFrame.add(saveButton);

                        saveButton.addActionListener(evSave -> {
                            try {
                                Statement stmtUpdate = conn.createStatement();
                                stmtUpdate.executeUpdate("UPDATE courses SET course_name = '" + textField.getText() + "' WHERE id = '" + courseId + "'");
                                JOptionPane.showMessageDialog(editFrame, "Course name updated!");
                                stmtUpdate.close();
                                editFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(editFrame, "Error updating course name.");
                            }
                        });

                        editFrame.setVisible(true);
                    });
                }

                courseFrame.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });



        creditHours.addActionListener(ev -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Get unique course IDs
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + facultyId + "'");
                ArrayList<String> courseIds = new ArrayList<>();
                while (rs1.next()) {
                    courseIds.add(rs1.getString("course_id"));
                }
                rs1.close();
                stmt1.close();

                // Get course names and credit hours
                HashMap<String, String> courseMap = new HashMap<>();
                HashMap<String, String> creditHoursMap = new HashMap<>();
                Statement stmt2 = conn.createStatement();
                for (String courseId : courseIds) {
                    ResultSet rs2 = stmt2.executeQuery("SELECT course_name, credit_hours FROM courses WHERE id = '" + courseId + "'");
                    if (rs2.next()) {
                        courseMap.put(courseId, rs2.getString("course_name"));
                        creditHoursMap.put(courseId, rs2.getString("credit_hours"));
                    }
                    rs2.close();
                }
                stmt2.close();

                // Create new frame for courses
                JFrame courseFrame = Frame.basicFrame("Courses in Faculty", 500, 500, false);

                int y = 20;
                for (String courseId : courseMap.keySet()) {
                    String courseName = courseMap.get(courseId);
                    String courseCreditHours = creditHoursMap.get(courseId);

                    JButton courseButton = new JButton(courseName);
                    courseButton.setBounds(50, y, 200, 30);
                    courseFrame.add(courseButton);
                    y += 50;

                    courseButton.addActionListener(e -> {
                        JFrame editFrame = Frame.basicFrame("Edit Credit Hours", 400, 200, false);

                        JLabel label = new JLabel("Credit Hours:");
                        label.setBounds(20, 20, 100, 25);
                        editFrame.add(label);

                        JTextField textField = new JTextField(courseCreditHours);
                        textField.setBounds(130, 20, 200, 25);
                        editFrame.add(textField);

                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(130, 60, 100, 30);
                        editFrame.add(saveButton);

                        saveButton.addActionListener(evSave -> {
                            try {
                                Statement stmtUpdate = conn.createStatement();
                                stmtUpdate.executeUpdate("UPDATE courses SET credit_hours = '" + textField.getText() + "' WHERE id = '" + courseId + "'");
                                JOptionPane.showMessageDialog(editFrame, "Credit hours updated!");
                                stmtUpdate.close();
                                editFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(editFrame, "Error updating credit hours.");
                            }
                        });

                        editFrame.setVisible(true);
                    });
                }

                courseFrame.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }


}
