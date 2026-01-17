-- Insert Courses for Fall 2025 Semester
-- Get active semester ID
SET @active_semester_id = (SELECT id FROM semesters WHERE is_active = TRUE LIMIT 1);
SET @cs_dept_id = (SELECT id FROM departments WHERE department_code = 'CS');

-- Course 1: Database Systems
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS301', 'Database Systems', 'Introduction to database design, SQL, and database management systems. Topics include relational model, normalization, query optimization, and transaction management.', 3, 40, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC001'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course1_id = LAST_INSERT_ID();

-- Course 2: Software Engineering
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS302', 'Software Engineering', 'Software development lifecycle, design patterns, testing strategies, and project management. Includes hands-on team project experience.', 4, 35, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC002'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course2_id = LAST_INSERT_ID();

-- Course 3: Data Structures and Algorithms
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS201', 'Data Structures and Algorithms', 'Fundamental data structures (arrays, linked lists, trees, graphs) and algorithms (sorting, searching, dynamic programming). Analysis of algorithm complexity.', 4, 50, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC003'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course3_id = LAST_INSERT_ID();

-- Course 4: Web Development
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS350', 'Web Development', 'Modern web development using HTML5, CSS3, JavaScript, and popular frameworks. Includes both frontend and backend development with databases.', 3, 30, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC004'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course4_id = LAST_INSERT_ID();

-- Course 5: Object-Oriented Programming
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS202', 'Object-Oriented Programming', 'Principles of OOP including encapsulation, inheritance, polymorphism. Design patterns and best practices using Java.', 3, 45, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC002'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course5_id = LAST_INSERT_ID();

-- Course 6: Computer Networks
INSERT INTO courses (course_code, course_name, description, credits, max_students, current_enrolled, status, lecturer_id, department_id, semester_id, created_at, updated_at) VALUES
('CS401', 'Computer Networks', 'Network protocols, TCP/IP, routing, network security, and cloud computing fundamentals.', 3, 40, 0, 'ACTIVE',
 (SELECT user_id FROM lecturers WHERE employee_id = 'LEC001'),
 @cs_dept_id,
 @active_semester_id,
 NOW(), NOW());

SET @course6_id = LAST_INSERT_ID();

-- Insert Schedules for Courses
-- CS301 - Database Systems: Monday & Wednesday 8:00-9:30
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course1_id, (SELECT id FROM classrooms WHERE room_number = 'LAB01'), 'MONDAY', '08:00:00', '09:30:00', NOW(), NOW()),
(@course1_id, (SELECT id FROM classrooms WHERE room_number = 'LAB01'), 'WEDNESDAY', '08:00:00', '09:30:00', NOW(), NOW());

-- CS302 - Software Engineering: Tuesday & Thursday 09:45-11:15
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course2_id, (SELECT id FROM classrooms WHERE room_number = 'A101'), 'TUESDAY', '09:45:00', '11:15:00', NOW(), NOW()),
(@course2_id, (SELECT id FROM classrooms WHERE room_number = 'A101'), 'THURSDAY', '09:45:00', '11:15:00', NOW(), NOW());

-- CS201 - Data Structures: Monday, Wednesday & Friday 11:30-13:00
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course3_id, (SELECT id FROM classrooms WHERE room_number = 'B201'), 'MONDAY', '11:30:00', '13:00:00', NOW(), NOW()),
(@course3_id, (SELECT id FROM classrooms WHERE room_number = 'B201'), 'WEDNESDAY', '11:30:00', '13:00:00', NOW(), NOW()),
(@course3_id, (SELECT id FROM classrooms WHERE room_number = 'B201'), 'FRIDAY', '11:30:00', '13:00:00', NOW(), NOW());

-- CS350 - Web Development: Tuesday & Thursday 13:15-14:45
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course4_id, (SELECT id FROM classrooms WHERE room_number = 'LAB02'), 'TUESDAY', '13:15:00', '14:45:00', NOW(), NOW()),
(@course4_id, (SELECT id FROM classrooms WHERE room_number = 'LAB02'), 'THURSDAY', '13:15:00', '14:45:00', NOW(), NOW());

-- CS202 - OOP: Monday & Wednesday 15:00-16:30
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course5_id, (SELECT id FROM classrooms WHERE room_number = 'A102'), 'MONDAY', '15:00:00', '16:30:00', NOW(), NOW()),
(@course5_id, (SELECT id FROM classrooms WHERE room_number = 'A102'), 'WEDNESDAY', '15:00:00', '16:30:00', NOW(), NOW());

-- CS401 - Computer Networks: Tuesday & Thursday 15:00-16:30
INSERT INTO schedules (course_id, classroom_id, day_of_week, start_time, end_time, created_at, updated_at) VALUES
(@course6_id, (SELECT id FROM classrooms WHERE room_number = 'B202'), 'TUESDAY', '15:00:00', '16:30:00', NOW(), NOW()),
(@course6_id, (SELECT id FROM classrooms WHERE room_number = 'B202'), 'THURSDAY', '15:00:00', '16:30:00', NOW(), NOW());

-- Sample Enrollments
-- Alice Wilson (Year 3) enrolls in advanced courses
INSERT INTO enrollments (student_id, course_id, status, enrolled_at, created_at, updated_at) VALUES
((SELECT user_id FROM students WHERE student_id = 'STU001'), @course1_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU001'), @course2_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU001'), @course4_id, 'APPROVED', NOW(), NOW(), NOW());

-- Bob Martinez (Year 2) enrolls in intermediate courses
INSERT INTO enrollments (student_id, course_id, status, enrolled_at, created_at, updated_at) VALUES
((SELECT user_id FROM students WHERE student_id = 'STU002'), @course3_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU002'), @course5_id, 'APPROVED', NOW(), NOW(), NOW());

-- Carol Garcia (Year 4) enrolls in advanced courses
INSERT INTO enrollments (student_id, course_id, status, enrolled_at, created_at, updated_at) VALUES
((SELECT user_id FROM students WHERE student_id = 'STU003'), @course1_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU003'), @course6_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU003'), @course2_id, 'APPROVED', NOW(), NOW(), NOW());

-- David Lee (Year 1) enrolls in foundational courses
INSERT INTO enrollments (student_id, course_id, status, enrolled_at, created_at, updated_at) VALUES
((SELECT user_id FROM students WHERE student_id = 'STU004'), @course3_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU004'), @course5_id, 'PENDING', NOW(), NOW(), NOW());

-- Emma Taylor (Year 2) enrolls in mixed courses
INSERT INTO enrollments (student_id, course_id, status, enrolled_at, created_at, updated_at) VALUES
((SELECT user_id FROM students WHERE student_id = 'STU005'), @course3_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU005'), @course4_id, 'APPROVED', NOW(), NOW(), NOW()),
((SELECT user_id FROM students WHERE student_id = 'STU005'), @course5_id, 'APPROVED', NOW(), NOW(), NOW());

-- Update current_enrolled count for courses
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course1_id AND status = 'APPROVED') WHERE id = @course1_id;
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course2_id AND status = 'APPROVED') WHERE id = @course2_id;
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course3_id AND status = 'APPROVED') WHERE id = @course3_id;
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course4_id AND status = 'APPROVED') WHERE id = @course4_id;
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course5_id AND status = 'APPROVED') WHERE id = @course5_id;
UPDATE courses SET current_enrolled = (SELECT COUNT(*) FROM enrollments WHERE course_id = @course6_id AND status = 'APPROVED') WHERE id = @course6_id;
