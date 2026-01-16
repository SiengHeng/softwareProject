package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.dto.StudentDTO;
import com.university.courseenrollment.demogradle.model.entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student createStudent(StudentDTO dto);
    Student updateStudent(Long id, StudentDTO dto);
    void deleteStudent(Long id);
    Optional<Student> getStudentById(Long id);
    Optional<Student> getStudentByStudentId(String studentId);
    Optional<Student> getStudentByUsername(String username);
    List<Student> getAllStudents();
    List<Student> getStudentsByDepartment(Long departmentId);
    List<Student> getStudentsByYearLevel(Integer yearLevel);
    void updateGPA(Long studentId, Double gpa);
    StudentDTO convertToDTO(Student student);
}