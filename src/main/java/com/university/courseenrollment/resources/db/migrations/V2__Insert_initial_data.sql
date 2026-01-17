-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'System Administrator'),
('ROLE_LECTURER', 'Lecturer/Instructor'),
('ROLE_STUDENT', 'Student');

-- Insert Departments
INSERT INTO departments (department_code, department_name, description) VALUES
('CS', 'Computer Science', 'Department of Computer Science and Software Engineering'),
('EE', 'Electrical Engineering', 'Department of Electrical Engineering'),
('ME', 'Mechanical Engineering', 'Department of Mechanical Engineering'),
('BA', 'Business Administration', 'Department of Business Administration'),
('MATH', 'Mathematics', 'Department of Mathematics');

-- Insert Semesters
INSERT INTO semesters (name, year, start_date, end_date, is_active) VALUES
('Fall', 2024, '2024-09-01', '2024-12-15', FALSE),
('Spring', 2025, '2025-01-15', '2025-05-15', FALSE),
('Fall', 2025, '2025-09-01', '2025-12-15', TRUE),
('Spring', 2026, '2026-01-15', '2026-05-15', FALSE);

-- Insert Time Slots
INSERT INTO time_slots (start_time, end_time, description) VALUES
('08:00:00', '09:30:00', 'Morning Slot 1'),
('09:45:00', '11:15:00', 'Morning Slot 2'),
('11:30:00', '13:00:00', 'Midday Slot'),
('13:15:00', '14:45:00', 'Afternoon Slot 1'),
('15:00:00', '16:30:00', 'Afternoon Slot 2'),
('16:45:00', '18:15:00', 'Evening Slot');

-- Insert Classrooms
INSERT INTO classrooms (room_number, building, capacity, is_available) VALUES
('A101', 'Main Building', 40, TRUE),
('A102', 'Main Building', 35, TRUE),
('A103', 'Main Building', 30, TRUE),
('B201', 'Science Building', 50, TRUE),
('B202', 'Science Building', 45, TRUE),
('B203', 'Science Building', 40, TRUE),
('C301', 'Engineering Building', 60, TRUE),
('C302', 'Engineering Building', 55, TRUE),
('LAB01', 'Computer Lab', 30, TRUE),
('LAB02', 'Computer Lab', 25, TRUE);

-- Insert Admin User (password: admin123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('admin', '$2a$10$lLPAHVfyIsn9cNuMx7BUJuvp.bGtjMIoR.ED.2pedt.PFm4ImT.0i', 'admin@university.edu', 'System', 'Administrator', '123-456-7890', TRUE, NOW(), NOW());

SET @admin_user_id = LAST_INSERT_ID();

INSERT INTO admins (user_id, admin_id) VALUES
(@admin_user_id, 'ADM001');

INSERT INTO user_roles (user_id, role_id) VALUES
(@admin_user_id, (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));

-- Insert Lecturers
-- Lecturer 1: Dr. John Smith (password: lecturer123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('john.smith', '$2a$10$UEYoOl02pEFWVs0EtMLqXOu3MuPQhc3Xdi97U1T9gIWVQXBFrmIB6', 'john.smith@university.edu', 'John', 'Smith', '123-456-7891', TRUE, NOW(), NOW());

SET @lecturer1_id = LAST_INSERT_ID();

INSERT INTO lecturers (user_id, employee_id, office_room, specialization, department_id) VALUES
(@lecturer1_id, 'LEC001', 'A-301', 'Database Systems', (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@lecturer1_id, (SELECT id FROM roles WHERE name = 'ROLE_LECTURER'));

-- Lecturer 2: Dr. Sarah Johnson (password: lecturer123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('sarah.johnson', '$2b$10$UEYoOl02pEFWVs0EtMLqXOu3MuPQhc3Xdi97U1T9gIWVQXBFrmIB6', 'sarah.johnson@university.edu', 'Sarah', 'Johnson', '123-456-7892', TRUE, NOW(), NOW());

SET @lecturer2_id = LAST_INSERT_ID();

INSERT INTO lecturers (user_id, employee_id, office_room, specialization, department_id) VALUES
(@lecturer2_id, 'LEC002', 'A-302', 'Software Engineering', (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@lecturer2_id, (SELECT id FROM roles WHERE name = 'ROLE_LECTURER'));

-- Lecturer 3: Dr. Michael Brown (password: lecturer123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('michael.brown', '$2b$10$UEYoOl02pEFWVs0EtMLqXOu3MuPQhc3Xdi97U1T9gIWVQXBFrmIB6', 'michael.brown@university.edu', 'Michael', 'Brown', '123-456-7893', TRUE, NOW(), NOW());

SET @lecturer3_id = LAST_INSERT_ID();

INSERT INTO lecturers (user_id, employee_id, office_room, specialization, department_id) VALUES
(@lecturer3_id, 'LEC003', 'B-201', 'Algorithms', (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@lecturer3_id, (SELECT id FROM roles WHERE name = 'ROLE_LECTURER'));

-- Lecturer 4: Dr. Emily Davis (password: lecturer123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('emily.davis', '$2b$10$UEYoOl02pEFWVs0EtMLqXOu3MuPQhc3Xdi97U1T9gIWVQXBFrmIB6', 'emily.davis@university.edu', 'Emily', 'Davis', '123-456-7894', TRUE, NOW(), NOW());

SET @lecturer4_id = LAST_INSERT_ID();

INSERT INTO lecturers (user_id, employee_id, office_room, specialization, department_id) VALUES
(@lecturer4_id, 'LEC004', 'B-202', 'Web Development', (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@lecturer4_id, (SELECT id FROM roles WHERE name = 'ROLE_LECTURER'));

-- Insert Students
-- Student 1: Alice Wilson (password: student123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('alice.wilson', '$2b$10$F54gPasFjEmRVKvZgBB/0eV/izhmyF/YNaWiYwqW864VGUExGcu3e', 'alice.wilson@student.edu', 'Alice', 'Wilson', '123-456-8001', TRUE, NOW(), NOW());

SET @student1_id = LAST_INSERT_ID();

INSERT INTO students (user_id, student_id, major, year_level, gpa, department_id) VALUES
(@student1_id, 'STU001', 'Computer Science', 3, 3.75, (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@student1_id, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));

-- Student 2: Bob Martinez (password: student123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('bob.martinez', '$2b$10$F54gPasFjEmRVKvZgBB/0eV/izhmyF/YNaWiYwqW864VGUExGcu3e', 'bob.martinez@student.edu', 'Bob', 'Martinez', '123-456-8002', TRUE, NOW(), NOW());

SET @student2_id = LAST_INSERT_ID();

INSERT INTO students (user_id, student_id, major, year_level, gpa, department_id) VALUES
(@student2_id, 'STU002', 'Computer Science', 2, 3.50, (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@student2_id, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));

-- Student 3: Carol Garcia (password: student123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('carol.garcia', '$2b$10$F54gPasFjEmRVKvZgBB/0eV/izhmyF/YNaWiYwqW864VGUExGcu3e', 'carol.garcia@student.edu', 'Carol', 'Garcia', '123-456-8003', TRUE, NOW(), NOW());

SET @student3_id = LAST_INSERT_ID();

INSERT INTO students (user_id, student_id, major, year_level, gpa, department_id) VALUES
(@student3_id, 'STU003', 'Computer Science', 4, 3.90, (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@student3_id, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));

-- Student 4: David Lee (password: student123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('david.lee', '$2b$10$F54gPasFjEmRVKvZgBB/0eV/izhmyF/YNaWiYwqW864VGUExGcu3e', 'david.lee@student.edu', 'David', 'Lee', '123-456-8004', TRUE, NOW(), NOW());

SET @student4_id = LAST_INSERT_ID();

INSERT INTO students (user_id, student_id, major, year_level, gpa, department_id) VALUES
(@student4_id, 'STU004', 'Computer Science', 1, 3.60, (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@student4_id, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));

-- Student 5: Emma Taylor (password: student123)
INSERT INTO users (username, password, email, first_name, last_name, phone_number, active, created_at, updated_at) VALUES
('emma.taylor', '$2b$10$F54gPasFjEmRVKvZgBB/0eV/izhmyF/YNaWiYwqW864VGUExGcu3e', 'emma.taylor@student.edu', 'Emma', 'Taylor', '123-456-8005', TRUE, NOW(), NOW());

SET @student5_id = LAST_INSERT_ID();

INSERT INTO students (user_id, student_id, major, year_level, gpa, department_id) VALUES
(@student5_id, 'STU005', 'Computer Science', 2, 3.85, (SELECT id FROM departments WHERE department_code = 'CS'));

INSERT INTO user_roles (user_id, role_id) VALUES
(@student5_id, (SELECT id FROM roles WHERE name = 'ROLE_STUDENT'));
