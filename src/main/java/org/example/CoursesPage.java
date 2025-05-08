package org.example;

import javax.swing.*;
import java.sql.*;

public class CoursesPage {
    JFrame frame;

    public CoursesPage(String studentId) {
        frame = Frame.basicFrame("My Courses", 500, 600, true);

        JLabel titleLabel = new JLabel("Registered Courses:");
        titleLabel.setBounds(50, 100, 400, 25);
        frame.add(titleLabel);

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT c.id, c.course_name, c.description, c.credit_hours, c.prerequisites, " +
                    "c.schedule " +
                    "FROM courses c " +
                    "INNER JOIN student_courses sc ON c.id = sc.course_id " +
                    "WHERE sc.student_id = '" + studentId + "'";
            ResultSet rs = stmt.executeQuery(query);

            int y = 60;  // Start from a fixed position
            while (rs.next()) {
                String courseInfo = "<html>Course ID: " + rs.getString("id") +
                        "<br>Title: " + rs.getString("course_name") +
                        "<br>Description: " + rs.getString("description") +
                        "<br>Credit Hours: " + rs.getString("credit_hours") +
                        "<br>Prerequisites: " + rs.getString("prerequisites") +
                        "<br>Schedule: " + rs.getString("schedule") + "</html>";

                JLabel courseLabel = new JLabel(courseInfo);
                courseLabel.setBounds(20, y, 450, 150);
                frame.add(courseLabel);

                y += 160;  // Increment the position for the next course
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }
}
