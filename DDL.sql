CREATE TABLE systemAdmin
(
    id            char(4) PRIMARY KEY NOT NULL,
    name          varchar(50)         NOT NULL,
    password      varchar(50)         NOT NULL,
    contact       char(11)            NOT NULL,
    email         varchar(50),
    securityLevel varchar(6)          NOT NULL,
    CHECK (securityLevel IN ('High', 'Medium', 'Low'))
);

CREATE TABLE faculties
(
    id        char(3) PRIMARY KEY NOT NULL,
    name      varchar(50)         NOT NULL,
    password  varchar(50)         NOT NULL,
    contact   char(11)            NOT NULL,
    email     varchar(50),
    expertise varchar(20),
    UNIQUE (name)
);

CREATE TABLE departments
(
    id         char(3) PRIMARY KEY NOT NULL,
    name       varchar(50)         NOT NULL,
    faculty_id varchar(4)          NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties (id),
    UNIQUE (name)
);

CREATE TABLE adminStaff
(
    id          char(3) PRIMARY KEY NOT NULL,
    name        varchar(50)         NOT NULL,
    password    varchar(50)         NOT NULL,
    contact     char(11)            NOT NULL,
    email       varchar(50),
    faculty     varchar(50)         NOT NULL,
    department  varchar(50)         NOT NULL,
    role        varchar(2)          NOT NULL,
    officeHours INT                 NOT NULL,
    FOREIGN KEY (faculty) REFERENCES faculties (name),
    FOREIGN KEY (department) REFERENCES departments (name),
    CHECK (role IN ('TA', 'DR'))
);

CREATE TABLE students
(
    id             char(4) PRIMARY KEY NOT NULL,
    name           varchar(50)         NOT NULL,
    password       varchar(50)         NOT NULL,
    contact        char(11)            NOT NULL,
    email          varchar(50),
    admissionDate  DATE,
    academicStatus varchar(12)         NOT NULL,
    faculty        varchar(50)         NOT NULL,
    department     varchar(50)         NOT NULL,
    FOREIGN KEY (faculty) REFERENCES faculties (name),
    FOREIGN KEY (department) REFERENCES departments (name),
    CHECK (academicStatus IN ('Active', 'Graduated', 'On Probation'))
);

CREATE TABLE courses
(
    id           char(3) PRIMARY KEY NOT NULL,
    course_name  varchar(50)         NOT NULL,
    description  varchar(200)        NOT NULL,
    credit_hours INT                 NOT NULL,
    schedule     VARCHAR(50)         NOT NULL
);

CREATE TABLE course_department
(
    course_id     char(3) NOT NULL,
    department_id char(3) NOT NULL,
    faculty_id    char(3) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (department_id) REFERENCES departments (id),
    FOREIGN KEY (faculty_id) REFERENCES faculties (id)
);

CREATE TABLE course_prerequisite
(
    course_id       char(3) NOT NULL,
    prerequisite_id char(3) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (id),
    FOREIGN KEY (prerequisite_id) REFERENCES courses (id)
);

CREATE TABLE student_course
(
    student_id char(4) NOT NULL,
    course_id  char(3) NOT NULL,
    grade      INT,
    status     varchar(10) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students (id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    CHECK (status IN ('Registered','Passed','Failed'))
);

CREATE TABLE system_permissions(
    id INT NOT NULL,
    allow_dropping INT NOT NULL,
    allow_registering INT NOT NULL,
    allow_updating_profile INT NOT NULL,
    allow_login INT NOT NULL,
    allow_reset INT NOT NULL
)