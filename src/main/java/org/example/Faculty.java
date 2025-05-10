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
                setOfficeHours(id);
            }
        });

        generateReports.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStudentRoster(id);
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
                        java.util.List<JCheckBox> completedCheckBoxes = new java.util.ArrayList<>();

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
                            String gradeQuery = "SELECT grade FROM student_courses WHERE student_id = '" + studentId + "' AND course_id = '" + finalCourseID + "' AND status = 'Registered'";
                            try (Connection gradeConn = DriverManager.getConnection("jdbc:sqlite:database.db");
                                 Statement gradeStmt = gradeConn.createStatement();
                                 ResultSet gradeRs = gradeStmt.executeQuery(gradeQuery)) {
                                if (gradeRs.next()) {
                                    grade = gradeRs.getString("grade");
                                } else {
                                    grade = "";
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

                            JLabel completedLabel = new JLabel("Completed:");
                            completedLabel.setBounds(30, yPos, 80, 25);
                            gradesFrame.add(completedLabel);

                            JCheckBox completedCheckBox = new JCheckBox();
                            completedCheckBox.setBounds(100, yPos, 25, 25);
                            gradesFrame.add(completedCheckBox);
                            completedCheckBoxes.add(completedCheckBox);

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
                                                "Invalid grade entered for student ID: " + studentId + ". Please enter a grade between 0 and 100.",
                                                "Input Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    String updateQuery = "UPDATE student_courses SET grade = '" + grade
                                            + "' WHERE student_id = '" + studentId + "' AND course_id = '"
                                            + finalCourseID + "'";
                                    try (Statement updateStmt = saveConn.createStatement()) {
                                        updateStmt.executeUpdate(updateQuery);
                                    }
                                    JCheckBox completedCheckBox = completedCheckBoxes.get(i);
                                    if (completedCheckBox.isSelected()) {
                                        String updateStatusQuery = "UPDATE student_courses SET status = 'Completed' WHERE student_id = '" + studentId + "' AND course_id = '" + finalCourseID + "'";
                                        try (PreparedStatement statusStmt = saveConn.prepareStatement(updateStatusQuery)) {
                                            statusStmt.executeUpdate();
                                        }
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

    private void manageCourse(String id) {
        JFrame frame = Frame.basicFrame("Manage Courses", 800, 700, false);

        JLabel titleLabel = new JLabel("Manage Courses");
        titleLabel.setBounds(30, 50, 250, 25);
        frame.add(titleLabel);

        JButton changeName = new JButton("Change course name");
        changeName.setBounds(50, 100, 200, 30);
        frame.add(changeName);

        JButton changeDescription = new JButton("Change Course Description");
        changeDescription.setBounds(50, 150, 200, 30);
        frame.add(changeDescription);

        JButton creditHours = new JButton("Change Credit Hours Course");
        creditHours.setBounds(50, 200, 200, 30);
        frame.add(creditHours);

        JButton schedule = new JButton("Change Course Schedule");
        schedule.setBounds(50, 250, 200, 30);
        frame.add(schedule);


        changeName.addActionListener(ev -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Get unique course IDs
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + id + "'");
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


        changeDescription.addActionListener(ev -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Get unique course IDs
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + id + "'");
                ArrayList<String> courseIds = new ArrayList<>();
                while (rs1.next()) {
                    courseIds.add(rs1.getString("course_id"));
                }
                rs1.close();
                stmt1.close();

                // Get course names and credit hours
                HashMap<String, String> courseMap = new HashMap<>();
                HashMap<String, String> descriptionMap = new HashMap<>();
                Statement stmt2 = conn.createStatement();
                for (String courseId : courseIds) {
                    ResultSet rs2 = stmt2.executeQuery("SELECT course_name, description FROM courses WHERE id = '" + courseId + "'");
                    if (rs2.next()) {
                        courseMap.put(courseId, rs2.getString("course_name"));
                        descriptionMap.put(courseId, rs2.getString("description"));
                    }
                    rs2.close();
                }
                stmt2.close();

                // Create new frame for courses
                JFrame courseFrame = Frame.basicFrame("Courses in Faculty", 500, 500, false);

                int y = 20;
                for (String courseId : courseMap.keySet()) {
                    String courseName = courseMap.get(courseId);
                    String descriptionHours = descriptionMap.get(courseId);

                    JButton courseButton = new JButton(courseName);
                    courseButton.setBounds(50, y, 200, 30);
                    courseFrame.add(courseButton);
                    y += 50;

                    courseButton.addActionListener(e -> {
                        JFrame editFrame = Frame.basicFrame("Edit Description", 400, 200, false);

                        JLabel label = new JLabel("Description:");
                        label.setBounds(20, 20, 100, 25);
                        editFrame.add(label);

                        JTextField textField = new JTextField(descriptionHours);
                        textField.setBounds(130, 20, 200, 25);
                        editFrame.add(textField);

                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(130, 60, 100, 30);
                        editFrame.add(saveButton);

                        saveButton.addActionListener(evSave -> {
                            try {
                                Statement stmtUpdate = conn.createStatement();
                                stmtUpdate.executeUpdate("UPDATE courses SET description = '" + textField.getText() + "' WHERE id = '" + courseId + "'");
                                JOptionPane.showMessageDialog(editFrame, "Description updated!");
                                stmtUpdate.close();
                                editFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(editFrame, "Error updating Description");
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
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + id + "'");
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


        schedule.addActionListener(ev -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

                // Get unique course IDs
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("SELECT DISTINCT course_id FROM course_department WHERE faculty_id = '" + id + "'");
                ArrayList<String> courseIds = new ArrayList<>();
                while (rs1.next()) {
                    courseIds.add(rs1.getString("course_id"));
                }
                rs1.close();
                stmt1.close();

                // Get course names and credit hours
                HashMap<String, String> courseMap = new HashMap<>();
                HashMap<String, String> scheduleMap = new HashMap<>();
                Statement stmt2 = conn.createStatement();
                for (String courseId : courseIds) {
                    ResultSet rs2 = stmt2.executeQuery("SELECT course_name, schedule FROM courses WHERE id = '" + courseId + "'");
                    if (rs2.next()) {
                        courseMap.put(courseId, rs2.getString("course_name"));
                        scheduleMap.put(courseId, rs2.getString("schedule"));
                    }
                    rs2.close();
                }
                stmt2.close();

                // Create new frame for courses
                JFrame courseFrame = Frame.basicFrame("Courses in Faculty", 500, 500, false);

                int y = 20;
                for (String courseId : courseMap.keySet()) {
                    String courseName = courseMap.get(courseId);
                    String courseSchedule = scheduleMap.get(courseId);

                    JButton courseButton = new JButton(courseName);
                    courseButton.setBounds(50, y, 200, 30);
                    courseFrame.add(courseButton);
                    y += 50;

                    courseButton.addActionListener(e -> {
                        JFrame editFrame = Frame.basicFrame("Edit schedule", 400, 200, false);

                        JLabel label = new JLabel("schedule:");
                        label.setBounds(20, 20, 100, 25);
                        editFrame.add(label);

                        JTextField textField = new JTextField(courseSchedule);
                        textField.setBounds(130, 20, 200, 25);
                        editFrame.add(textField);

                        JButton saveButton = new JButton("Save");
                        saveButton.setBounds(130, 60, 100, 30);
                        editFrame.add(saveButton);

                        saveButton.addActionListener(evSave -> {
                            try {
                                Statement stmtUpdate = conn.createStatement();
                                stmtUpdate.executeUpdate("UPDATE courses SET schedule = '" + textField.getText() + "' WHERE id = '" + courseId + "'");
                                JOptionPane.showMessageDialog(editFrame, "Schedule updated!");
                                stmtUpdate.close();
                                editFrame.dispose();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(editFrame, "Error updating Schedule.");
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

    private void setOfficeHours(String id) {
        JFrame frame = Frame.basicFrame("Set Office Hours", 800, 700, false);

        JLabel label = new JLabel("Set staff office hours here:");
        label.setBounds(10, 10, 250, 30);
        frame.add(label);

        JButton drButton = new JButton("Doctors");
        drButton.setBounds(130, 60, 100, 30);
        frame.add(drButton);

        JButton taButton = new JButton("TAs");
        taButton.setBounds(130, 100, 100, 30);
        frame.add(taButton);


        drButton.addActionListener(ae -> {
            JFrame roleFrame = Frame.basicFrame("Doctors at the faculty", 600, 700, false);

            java.util.List<JTextField> fields = new ArrayList<>();

            try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                 Statement stmt = con.createStatement()) {

                String role = "Doctor";

                String query = "SELECT id, name, officeHours, department FROM adminstaff WHERE faculty = '" + getFacultyName(id) + "' AND role = '" + role + "'";
                ResultSet rs = stmt.executeQuery(query);

                int yPosition = 50;
                while (rs.next()) {
                    String staffId = rs.getString("id");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    int hours = rs.getInt("officeHours");

                    JLabel staffLabel = new JLabel("Name: " + name + ", ID: " + staffId + ", Department: " + department + ", Office Hours: ");
                    staffLabel.setBounds(10, yPosition, 450, 30);

                    JTextField hoursField = new JTextField(String.valueOf(hours), 5);
                    hoursField.setBounds(400, yPosition + 4, 60, 25);
                    hoursField.setName(staffId);

                    fields.add(hoursField);

                    yPosition += 40;

                    roleFrame.add(staffLabel);
                    roleFrame.add(hoursField);
                }

                JButton saveButton = new JButton("Save");
                saveButton.setBounds(250, yPosition + 20, 100, 30);
                roleFrame.add(saveButton);

                saveButton.addActionListener(saveEvent -> {
                    for (JTextField field : fields) {
                        String hoursText = field.getText();
                        int hoursInput;
                        try {
                            hoursInput = Integer.parseInt(hoursText);
                            if (hoursInput < 0 || hoursInput > 72) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(roleFrame,
                                    "Invalid office hours entered for staff ID: " + field.getName() + ". Please enter office hours between 0 and 72.",
                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    try (Connection updateCon = DriverManager.getConnection("jdbc:sqlite:database.db");
                         Statement updateStmt = updateCon.createStatement()) {

                        for (JTextField field : fields) {
                            String staffId = field.getName();
                            int newHours = Integer.parseInt(field.getText());

                            String updateQuery = "UPDATE adminstaff SET officeHours = " + newHours + " WHERE id = '" + staffId + "'";
                            updateStmt.executeUpdate(updateQuery);
                        }
                        JOptionPane.showMessageDialog(roleFrame, "Office hours updated successfully.");
                        roleFrame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(roleFrame, "Failed to update office hours.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(roleFrame, "Failed to load staff office hours.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            roleFrame.setLayout(null);
            roleFrame.setVisible(true);
        });


        taButton.addActionListener(ae -> {
            JFrame roleFrame = Frame.basicFrame("TA at the faculty", 600, 700, false);

            java.util.List<JTextField> fields = new ArrayList<>();

            try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db");
                 Statement stmt = con.createStatement()) {

                String role = "TA";

                String query = "SELECT id, name, officeHours, department FROM adminstaff WHERE faculty = '" + getFacultyName(id) + "' AND role = '" + role + "'";
                ResultSet rs = stmt.executeQuery(query);

                int yPosition = 50;
                while (rs.next()) {
                    String staffId = rs.getString("id");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    int hours = rs.getInt("officeHours");

                    JLabel staffLabel = new JLabel("Name: " + name + ", ID: " + staffId + ", Department: " + department + ", Office Hours: ");
                    staffLabel.setBounds(10, yPosition, 450, 30);

                    JTextField hoursField = new JTextField(String.valueOf(hours), 5);
                    hoursField.setBounds(400, yPosition + 4, 60, 25);
                    hoursField.setName(staffId);

                    fields.add(hoursField);

                    yPosition += 40;

                    roleFrame.add(staffLabel);
                    roleFrame.add(hoursField);
                }

                JButton saveButton = new JButton("Save");
                saveButton.setBounds(250, yPosition + 20, 100, 30);
                roleFrame.add(saveButton);

                saveButton.addActionListener(saveEvent -> {
                    for (JTextField field : fields) {
                        String hoursText = field.getText();
                        int hoursInput;
                        try {
                            hoursInput = Integer.parseInt(hoursText);
                            if (hoursInput < 0 || hoursInput > 72) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(roleFrame,
                                    "Invalid office hours entered for staff ID: " + field.getName() + ". Please enter office hours between 0 and 72.",
                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    try (Connection updateCon = DriverManager.getConnection("jdbc:sqlite:database.db");
                         Statement updateStmt = updateCon.createStatement()) {

                        for (JTextField field : fields) {
                            String staffId = field.getName();
                            int newHours = Integer.parseInt(field.getText());

                            String updateQuery = "UPDATE adminstaff SET officeHours = " + newHours + " WHERE id = '" + staffId + "'";
                            updateStmt.executeUpdate(updateQuery);
                        }
                        JOptionPane.showMessageDialog(roleFrame, "Office hours updated successfully.");
                        roleFrame.dispose();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(roleFrame, "Failed to update office hours.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(roleFrame, "Failed to load staff office hours.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            roleFrame.setLayout(null);
            roleFrame.setVisible(true);
        });

        frame.setVisible(true);
    }

    private void viewStudentRoster(String id) {
        JFrame frame = Frame.basicFrame("View Student Roster", 800, 700, false);
        frame.setLayout(new BorderLayout());  // Use BorderLayout for frame

        // Label at the top
        JLabel label = new JLabel("Student Roster for " + getFacultyName(id) + ":");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Remove default border
        frame.add(label, BorderLayout.NORTH);

        // Panel to hold student rows
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Remove padding and border

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border of JScrollPane
        frame.add(scrollPane, BorderLayout.CENTER);

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db")) {
            String query = "SELECT id, name, department FROM students WHERE faculty = '" + getFacultyName(id) + "'";
            PreparedStatement ps = con.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String studentId = rs.getString("id");
                String studentName = rs.getString("name");
                String department = rs.getString("department");

                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Optional: To give small margin between rows

                JLabel studentLabel = new JLabel("ID: " + studentId + ", Name: " + studentName + ", Department: " + department);
                row.add(studentLabel);

                panel.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load student roster.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.setVisible(true);
    }

    private String getFacultyName(String id) {
        String facultyName = "";
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:database.db")) {
            String query = "SELECT name FROM faculties WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    facultyName = rs.getString("name");
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return facultyName;
    }

}
