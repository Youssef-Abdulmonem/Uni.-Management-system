package org.example;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class Student extends User {
    private JFrame frame;

    private String password, name, contact, email, admissionDate, academicStatus, faculty, department;

    public Student(String id) {
        super(id);

        frame = Frame.basicFrame("Student Page", 800, 700, true);


        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT name, password, contact, email, admissionDate, academicStatus, faculty, department " +
                             "FROM students WHERE id = ?"
             )) {


            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    password = rs.getString("password");
                    contact = rs.getString("contact");
                    email = rs.getString("email");
                    admissionDate = rs.getString("admissionDate");
                    academicStatus = rs.getString("academicStatus");
                    faculty = rs.getString("faculty");
                    department = rs.getString("department");
                }
            }

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

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email, "students");
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


    private void registerForCourse(String id) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT allow_registering FROM system_permissions WHERE id = 1");
            while (rs.next()) {
                boolean permission = rs.getBoolean("allow_registering");
                if(!permission) {
                    JOptionPane.showMessageDialog(null, "Registering courses isn't allowed!");
                    return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        frame = Frame.basicFrame("Register for Courses", 600, 600, false);

        JLabel titleLabel = new JLabel("Select Courses to Register:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        java.util.List<JCheckBox> checkboxes = new java.util.ArrayList<>();


        String query =
                "SELECT c.id, c.course_name " +
                        "FROM courses c " +
                        "JOIN course_department cd ON c.id = cd.course_id " +
                        "JOIN departments d ON cd.department_id = d.id " +
                        "JOIN students s ON s.department = d.name " +
                        "WHERE s.id = ? " +
                        "AND c.id NOT IN (SELECT course_id FROM student_courses WHERE student_id = ?) " +
                        "AND (" +
                        "    c.id NOT IN (SELECT course_id FROM course_prerequisites) " +
                        "    OR EXISTS (" +
                        "        SELECT 1 FROM course_prerequisites cp " +
                        "        JOIN student_courses sc ON cp.prerequisite_id = sc.course_id " +
                        "        WHERE cp.course_id = c.id " +
                        "        AND sc.student_id = ? " +
                        "        AND sc.grade >= 50" +
                        "    )" +
                        ")";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            pstmt.setString(2, id);
            pstmt.setString(3, id);

            ResultSet rs = pstmt.executeQuery();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 400, 200, 30);
        saveButton.addActionListener(e -> {

            String insertQuery = "INSERT INTO student_courses (student_id, course_id, grade, status) VALUES (?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
                 PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

                for (JCheckBox checkBox : checkboxes) {
                    if (checkBox.isSelected()) {

                        String[] parts = checkBox.getText().split(" - ");
                        String courseId = parts[0];

                        pstmt.setString(1, id);
                        pstmt.setString(2, courseId);
                        pstmt.setInt(3, 0);
                        pstmt.setString(4, "Registered");

                        pstmt.executeUpdate();
                    }
                }

                pstmt.close();
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


    private void dropCourse(String id) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT allow_dropping FROM system_permissions WHERE id = 1");
            while (rs.next()) {
                boolean permission = rs.getBoolean("allow_dropping");
                if(!permission) {
                    JOptionPane.showMessageDialog(null, "Dropping courses isn't allowed!");
                    return;
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        frame = Frame.basicFrame("Drop Courses", 600, 600, false);

        JLabel titleLabel = new JLabel("Select Courses to Drop:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        java.util.List<JCheckBox> checkboxes = new java.util.ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {
            String query =
                    "SELECT c.id, c.course_name " +
                            "FROM courses c " +
                            "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                            "WHERE sc.student_id = ? AND sc.status = 'Registered'";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, id);

                ResultSet rs = pstmt.executeQuery();

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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 400, 200, 30);
        saveButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {
                String deleteQuery = "DELETE FROM student_courses WHERE student_id = ? AND course_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(deleteQuery);

                for (JCheckBox checkBox : checkboxes) {
                    if (!checkBox.isSelected()) {
                        String[] parts = checkBox.getText().split(" - ");

                        String courseId = parts[0];

                        pstmt.setString(1, id);
                        pstmt.setString(2, courseId);

                        pstmt.executeUpdate();
                    }
                }

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


    private void viewGrades(String id) {
        frame = Frame.basicFrame("View Grades", 650, 600, false);

        JLabel titleLabel = new JLabel("Your Registered Courses and Grades:");
        titleLabel.setBounds(20, 20, 400, 25);
        frame.add(titleLabel);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

            String query = "SELECT c.id, c.course_name, c.description, c.credit_hours, c.schedule, " +
                    "sc.grade, sc.status " +
                    "FROM courses c " +
                    "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, id);
                ResultSet rs = pstmt.executeQuery();

                int yPosition = 60;

                while (rs.next()) {
                    String courseId = rs.getString("id");
                    String courseName = rs.getString("course_name");
                    String description = rs.getString("description");
                    String creditHours = rs.getString("credit_hours");
                    String schedule = rs.getString("schedule");
                    String grade = rs.getString("grade");
                    double numericGrade = 0;


                    try {
                        numericGrade = Double.parseDouble(grade);
                    } catch (Exception ex) {
                        numericGrade = 0;
                    }

                    String courseInfo = "ID: " + courseId +
                            ", Title: " + courseName +
                            ", Credits: " + creditHours +
                            ", Schedule: " + schedule +
                            ", Grade: " + grade + " (" + convertToGrade(numericGrade) + ")" +
                            ", Status: " + rs.getString("status");

                    JLabel courseLabel = new JLabel(courseInfo);
                    courseLabel.setBounds(20, yPosition, 600, 25);
                    frame.add(courseLabel);

                    yPosition += 30;
                }

                rs.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }


    private void calculateGPA(String id) {
        frame = Frame.basicFrame("Your GPA", 400, 200, false);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

            String query = "SELECT c.credit_hours, sc.grade " +
                    "FROM courses c " +
                    "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = ? AND sc.status = 'Completed'";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, id);
                ResultSet rs = pstmt.executeQuery();

                double totalPoints = 0.0;
                int totalCredits = 0;

                while (rs.next()) {

                    String gradeStr = rs.getString("grade");
                    double gradeValue = 0;


                    try {
                        gradeValue = Double.parseDouble(gradeStr);
                    } catch (Exception ex) {
                        gradeValue = 0;
                    }

                    if (gradeValue == 0.0)
                        continue;

                    int creditHours = rs.getInt("credit_hours");

                    totalPoints += convertToPoints(gradeValue) * creditHours;
                    totalCredits += creditHours;
                }

                double gpa = (totalCredits > 0) ? (totalPoints / totalCredits) : 0.0;

                JLabel gpaLabel = new JLabel("Yours CGPA = " + String.format("%.2f", gpa));
                gpaLabel.setBounds(20, 60, 200, 25);
                frame.add(gpaLabel);

                rs.close();
            }

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