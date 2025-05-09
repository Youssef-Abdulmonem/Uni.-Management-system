package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminStaff extends User {
    JFrame frame;

    String password, name, contact, email, role, officeHours, faculty, department;

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
                officeHours = rs.getString("officeHours");
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
        JFrame frame = Frame.basicFrame("Register Student", 400, 500, true);
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
                String checkStudent = "SELECT id FROM students WHERE id = '" + student + "'";
                try (PreparedStatement ps = conn.prepareStatement(checkStudent)) {
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Student ID not found.");
                        return;
                    }
                }

                // Check if course exists
                String checkCourse = "SELECT id FROM courses WHERE id = '" + course + "'";
                try (PreparedStatement ps = conn.prepareStatement(checkCourse)) {
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Course ID not found.");
                        return;
                    }
                }

                // Check if already registered
                String checkDuplicate = "SELECT student_id, course_id FROM student_courses WHERE student_id = '" + student + "'AND course_id = '" + course + "'";
                try (PreparedStatement ps = conn.prepareStatement(checkDuplicate)) {
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(null, "Student already registered for this course.");
                        return;
                    }
                }

                // prerequisites
                String checkPrerequisites = "SELECT course_id, prerequisite_id FROM course_prerequisites WHERE course_id = '" + course + "'";
                try (PreparedStatement ps = conn.prepareStatement(checkPrerequisites)) {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String prerequisite = rs.getString("prerequisite_id");
                        String checkPrereq = "SELECT status FROM student_courses WHERE student_id = '" + student + "'AND course_id = '" + prerequisite + "'AND status = 'Completed' AND grade >= 50";
                        try (PreparedStatement ps2 = conn.prepareStatement(checkPrereq)) {
                            ResultSet rs2 = ps2.executeQuery();
                            if (!rs2.next()) {
                                JOptionPane.showMessageDialog(null, "Student must complete prerequisite course before registering for this course.");
                                return;
                            }
                        } catch (SQLException ex) {}
                    }
                }

                // Register student
                String insertQuery = "INSERT INTO student_courses (student_id, course_id, status) VALUES ('" + student + "', '" + course + "', 'Registered')";
                try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
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
        JFrame registerFrame = Frame.basicFrame("Register New Course", 400, 500, true);

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
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id FROM courses WHERE id LIKE 'C%'");

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
                stmt.close();
                conn.close();

                Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement stm = con.createStatement();

                String insertQuery = "INSERT INTO courses (id, course_name, description, credit_hours, schedule) " +
                        "VALUES ('" + newId + "', '" + name + "', '" + description + "', '" + credit + "', '" + schedule + "')";

                stm.executeUpdate(insertQuery);

                JOptionPane.showMessageDialog(registerFrame, "Course Created with ID: " + newId);
                registerFrame.dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registerFrame, "Failed to create course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerFrame.setVisible(true);

    }

    private void assignFaculty(String id) {

    }

    private void generateReports(String id) {

    }

}
