INSERT INTO faculties (id, name, password, contact, email, expertise)
VALUES ('F01', 'Engineering', 'engpass123', '01012345678', 'eng.dean@univ.edu', 'Civil Structures'),
       ('F02', 'Arts', 'artspass123', '01176543210', 'arts.dean@univ.edu', 'Literature & Theory'),
       ('F03', 'Science', 'scipass123', '01211223344', 'sci.dean@univ.edu', 'Theoretical Physics'),
       ('F04', 'FCDS', 'fcdspass123', '01544556677', 'fcds.dean@univ.edu', 'Data Analytics');

INSERT INTO departments (id, name, faculty_id)
VALUES ('D01', 'Computer Science', 'F04'),
       ('D02', 'Mechanical Engineering', 'F01'),
       ('D03', 'History', 'F02'),
       ('D04', 'Physics', 'F03');

INSERT INTO courses (id, course_name, description, credit_hours, schedule)
VALUES ('C01', 'Intro to Programming', 'Fundamental concepts of structured programming and logic.', 3,
        'Mon/Wed 10:00-11:30'),
       ('C02', 'Data Structures', 'Implementation and analysis of core data structures.', 4, 'Tue/Thu 11:30-13:00'),
       ('C03', 'World History', 'A survey of global historical developments from 1500 to present.', 3,
        'Tue/Thu 14:00-15:30'),
       ('C04', 'Modern Art', 'Study of 20th century art movements and key figures.', 3, 'Wed 16:00-19:00'),
       ('C05', 'Classical Mechanics', 'Introduction to Newtonian physics and dynamics.', 4, 'Mon/Wed/Fri 09:00-10:00'),
       ('C06', 'Thermo Dynamics', 'Principles of heat and energy transfer in engineering systems.', 4,
        'Mon/Wed 13:00-15:00');

INSERT INTO systemAdmin (id, name, password, contact, email, securityLevel)
VALUES ('SA01', 'Aisha Hassan', 'admsecure!', '01010000000', 'aisha.h@univ.edu', 'High'),
       ('SA02', 'Omar Selim', 'admmedium', '01120000000', 'omar.s@univ.edu', 'Medium'),
       ('SA03', 'Youssef Ehab', 'admlow', '01230000000', 'youssef.e@univ.edu', 'Low');

INSERT INTO adminStaff (id, name, password, contact, email, faculty, department, role, officeHours)
VALUES ('S01', 'Hoda Kamal', 'staffpass1', '01040000000', 'hoda.k@univ.edu', 'FCDS', 'Computer Science', 'DR', 10),
       ('S02', 'Tarek Fawzy', 'staffpass2', '01150000000', 'tarek.f@univ.edu', 'Science', 'Physics', 'TA', 5),
       ('S03', 'Karim Mostafa', 'staffpass3', '01260000000', 'karim.m@univ.edu', 'Engineering',
        'Mechanical Engineering', 'DR', 8),
       ('S04', 'Samia Adel', 'staffpass4', '01570000000', 'samia.a@univ.edu', 'Arts', 'History', 'TA', 6);

INSERT INTO students (id, name, password, contact, email, admissionDate, academicStatus, faculty, department)
VALUES ('ST01', 'Amr Mohamed', 'stu1pass', '01011111111', 'a.mohamed@univ.edu', '2023-09-01', 'Active', 'FCDS',
        'Computer Science'),
       ('ST02', 'Layla Said', 'stu2pass', '01122222222', 'l.said@univ.edu', '2022-09-01', 'Active', 'Science',
        'Physics'),
       ('ST03', 'Khaled Ali', 'stu3pass', '01233333333', 'k.ali@univ.edu', '2020-09-01', 'Graduated', 'Arts',
        'History'),
       ('ST04', 'Nour Tamer', 'stu4pass', '01544444444', 'n.tamer@univ.edu', '2023-09-01', 'Active', 'Engineering',
        'Mechanical Engineering'),
       ('ST05', 'Moustafa Emad', 'stu5pass', '01055555555', 'm.emad@univ.edu', '2024-01-15', 'On Probation', 'FCDS',
        'Computer Science'),
       ('ST06', 'Sara Ibrahim', 'stu6pass', '01166666666', 'sara.i@univ.edu', '2022-01-15', 'Active', 'Science',
        'Physics'),
       ('ST07', 'Rania Magdy', 'stu7pass', '01277777777', 'r.magdy@univ.edu', '2023-09-01', 'Active', 'Arts',
        'History'),
       ('ST08', 'Ahmed Galal', 'stu8pass', '01588888888', 'a.galal@univ.edu', '2021-09-01', 'Graduated', 'FCDS',
        'Computer Science'),
       ('ST09', 'Mona Yasser', 'stu9pass', '01099999999', 'm.yasser@univ.edu', '2022-09-01', 'Active', 'Engineering',
        'Mechanical Engineering'),
       ('ST10', 'Emad Wael', 'stu10pass', '01100000000', 'e.wael@univ.edu', '2023-01-15', 'Active', 'Science',
        'Physics');

INSERT INTO course_department (course_id, department_id, faculty_id)
VALUES ('C01', 'D01', 'F04'),
       ('C02', 'D01', 'F04'),
       ('C03', 'D03', 'F02'),
       ('C04', 'D03', 'F02'),
       ('C05', 'D04', 'F03'),
       ('C06', 'D02', 'F01');

INSERT INTO course_prerequisite (course_id, prerequisite_id)
VALUES ('C02', 'C01'),
       ('C04', 'C03');

INSERT INTO student_course (student_id, course_id, grade, status)
VALUES ('ST01', 'C01', 85, 'Passed'),
       ('ST01', 'C02', NULL, 'Registered'),
       ('ST02', 'C05', 72, 'Passed'),
       ('ST03', 'C03', 90, 'Passed'),
       ('ST04', 'C06', 60, 'Passed'),
       ('ST05', 'C01', 70, 'Passed'),
       ('ST05', 'C02', 45, 'Failed'),
       ('ST06', 'C05', NULL, 'Registered'),
       ('ST07', 'C03', 88, 'Passed'),
       ('ST07', 'C04', NULL, 'Registered'),
       ('ST08', 'C01', 78, 'Passed'),
       ('ST10', 'C05', 55, 'Passed');

INSERT INTO system_permissions (id, allow_dropping, allow_registering, allow_updating_profile, allow_login, allow_reset)
VALUES (1,1,1,1,1,1);