package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminStaff extends User {
    private JFrame frame;

    private String password, name, contact, email, role, officeHours, faculty, department;

    public AdminStaff(String id) {
        super(id);

        frame = Frame.basicFrame("Admin Staff Page", 800, 700, true);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

            String query =
                    "SELECT name, password, contact, email, faculty, department, role, officeHours " +
                            "FROM adminstaff WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, id);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    name = rs.getString("name");
                    password = rs.getString("password");
                    contact = rs.getString("contact");
                    email = rs.getString("email");
                    officeHours = rs.getString("officeHours");
                    role = rs.getString("role");
                    faculty = rs.getString("faculty");
                    department = rs.getString("department");
                }

                rs.close();
            }

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

        JButton updateProfileButton = updateProfile(frame, id, password, contact, email, "adminstaff");
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
                generateReports();
            }
        });

        frame.setVisible(true);
    }
    private void registerStudent(String adminId) {
        JFrame frame = Frame.basicFrame("Register Student", 400, 500, false);
        JLabel studentId = new JLabel("Student ID: ");
        JTextField sid = new JTextField();
        JLabel courseId = new JLabel("Course ID: ");
        JTextField cid = new JTextField();
        JButton saveButton = new JButton("Save");
        studentId.setBounds(30, 30, 120, 25);
        sid.setBounds(150, 30, 120, 25);
        courseId.setBounds(30, 70, 120, 25);
        cid.setBounds(150, 70, 120, 25);
        saveButton.setBounds(150, 320, 100, 30);
        frame.add(studentId);
        frame.add(sid);
        frame.add(courseId);
        frame.add(cid);
        frame.add(saveButton);

        saveButton.addActionListener(e -> {
            String student = sid.getText();
            String course = cid.getText();

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

                // Check if student exists
                String checkStudent = "SELECT id FROM students WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkStudent)) {
                    ps.setString(1, student);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Student ID not found.");
                        return;
                    }
                }

                // Check if course exists
                String checkCourse = "SELECT id FROM courses WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkCourse)) {
                    ps.setString(1, course);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Course ID not found.");
                        return;
                    }
                }

                // Check if already registered
                String checkDuplicate = "SELECT student_id, course_id FROM student_courses WHERE student_id = ? AND course_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkDuplicate)) {
                    ps.setString(1, student);
                    ps.setString(2, course);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Student already registered for this course.");
                        return;
                    }
                }

                // prerequisites
                String checkPrerequisites = "SELECT prerequisite_id FROM course_prerequisites WHERE course_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkPrerequisites)) {
                    ps.setString(1, course);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String prerequisite = rs.getString("prerequisite_id");

                        String checkPrereq = "SELECT status FROM student_courses WHERE student_id = ? AND course_id = ? AND status = 'Completed' AND grade >= 50";

                        try (PreparedStatement ps2 = conn.prepareStatement(checkPrereq)) {
                            ps2.setString(1, student);
                            ps2.setString(2, prerequisite);
                            ResultSet rs2 = ps2.executeQuery();

                            if (!rs2.next()) {
                                JOptionPane.showMessageDialog(null, "Student must complete prerequisite course before registering.");
                                return;
                            }
                        }
                    }
                }

                // Register student
                String insertQuery = "INSERT INTO student_courses (student_id, course_id, status) VALUES (?, ?, 'Registered')";
                try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                    ps.setString(1, student);
                    ps.setString(2, course);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Student registered successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error registering student.");
            }

            frame.dispose();
        });

        frame.setVisible(true);
    }


    private void createCourse(String id) {
        JFrame registerFrame = Frame.basicFrame("Create New Course", 400, 500, false);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JLabel creditLabel = new JLabel("Credit Hours:");
        JTextField creditField = new JTextField();

        JLabel scheduleLabel = new JLabel("Schedule:");
        JTextField scheduleField = new JTextField();

        JButton saveButton = new JButton("Save");

        nameLabel.setBounds(30, 30, 120, 25);
        nameField.setBounds(150, 30, 200, 25);

        descriptionLabel.setBounds(30, 70, 120, 25);
        descriptionField.setBounds(150, 70, 200, 25);

        creditLabel.setBounds(30, 110, 120, 25);
        creditField.setBounds(150, 110, 200, 25);

        scheduleLabel.setBounds(30, 150, 120, 25);
        scheduleField.setBounds(150, 150, 200, 25);

        saveButton.setBounds(150, 320, 100, 30);

        registerFrame.add(nameLabel);
        registerFrame.add(nameField);

        registerFrame.add(descriptionLabel);
        registerFrame.add(descriptionField);

        registerFrame.add(creditLabel);
        registerFrame.add(creditField);

        registerFrame.add(scheduleLabel);
        registerFrame.add(scheduleField);

        registerFrame.add(saveButton);

        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String credit = creditField.getText();
            String schedule = scheduleField.getText();

            int maxNumber = 0;
            String newId = "";

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                String selectQuery = "SELECT id FROM courses WHERE id LIKE 'C%'";
                PreparedStatement psSelect = conn.prepareStatement(selectQuery);
                ResultSet rs = psSelect.executeQuery();

                while (rs.next()) {
                    String existingId = rs.getString("id");
                    int num = Integer.parseInt(existingId.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                }

                int newNumber = maxNumber + 1;
                newId = String.format("C%02d", newNumber);

                rs.close();
                psSelect.close();
                conn.close();

                Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");

                String insertCourse = "INSERT INTO courses (id, course_name, description, credit_hours, schedule) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psInsertCourse = con.prepareStatement(insertCourse);
                psInsertCourse.setString(1, newId);
                psInsertCourse.setString(2, name);
                psInsertCourse.setString(3, description);
                psInsertCourse.setString(4, credit);
                psInsertCourse.setString(5, schedule);
                psInsertCourse.executeUpdate();
                psInsertCourse.close();

                String insertDept = "INSERT INTO course_department (course_id, department_id, faculty_id) VALUES (?, ?, ?)";
                PreparedStatement psDept = con.prepareStatement(insertDept);
                psDept.setString(1, newId);
                psDept.setString(2, department);
                psDept.setString(3, faculty);
                psDept.executeUpdate();
                psDept.close();

                JOptionPane.showMessageDialog(registerFrame, "Course Created with ID: " + newId);
                registerFrame.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registerFrame, "Failed to create course.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        registerFrame.setVisible(true);
    }

    private void assignFaculty(String adminId) {
        JFrame assignFrame = Frame.basicFrame("Assign Faculty", 400, 300, false);

        JLabel facultyLabel = new JLabel("Faculty:");
        JTextField facultyField = new JTextField();

        JButton saveButton = new JButton("Save");

        facultyLabel.setBounds(30, 50, 120, 25);
        facultyField.setBounds(150, 50, 200, 25);

        saveButton.setBounds(150, 100, 100, 30);

        assignFrame.add(facultyLabel);
        assignFrame.add(facultyField);

        assignFrame.add(saveButton);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            String query = "SELECT faculty FROM adminstaff WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, adminId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String currentFaculty = rs.getString("faculty");
                facultyField.setText(currentFaculty);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(assignFrame, "Failed to fetch data.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        saveButton.addActionListener(e -> {
            String newFaculty = facultyField.getText();

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db")) {

                String updateQuery = "UPDATE adminstaff SET faculty = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(updateQuery);

                ps.setString(1, newFaculty);
                ps.setString(2, adminId);

                ps.executeUpdate();
                ps.close();

                JOptionPane.showMessageDialog(assignFrame, "Faculty updated successfully.");
                assignFrame.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(assignFrame, "Failed to update faculty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });



        assignFrame.setVisible(true);
    }

    private void generateReports() {
        JFrame reportFrame = Frame.basicFrame("Student Courses Report", 800, 500, false);

        JLabel header = new JLabel("Report of Students");
        header.setBounds(20, 20, 700, 20);
        reportFrame.add(header);

        int yPosition = 50;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT sc.student_id, s.name AS student_name, sc.course_id, c.course_name AS course_name, sc.grade, sc.status " +
                     "FROM student_courses sc " +
                     "JOIN students s ON sc.student_id = s.id " +
                     "JOIN courses c ON sc.course_id = c.id")) {

            while (rs.next()) {
                String studentId = rs.getString("student_id");
                String studentName = rs.getString("student_name");
                String courseId = rs.getString("course_id");
                String courseName = rs.getString("course_name");
                String grade = rs.getString("grade");
                String status = rs.getString("status");

                String line = "Student ID: " + studentId + " ,Name: " + studentName + " ,Course ID: " + courseId + " ,Name: " + courseName + " ,Grade: " + grade + " ,Status: " + status;
                JLabel dataLabel = new JLabel(line);
                dataLabel.setBounds(20, yPosition, 700, 20);
                reportFrame.add(dataLabel);

                yPosition += 30;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(reportFrame, "Failed to generate report.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        reportFrame.setVisible(true);
    }


}
