package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByUsername(String username);
    Optional<Student> findByEmail(String email);
    List<Student> findByDepartmentId(Long departmentId);
    List<Student> findByYearLevel(Integer yearLevel);
    boolean existsByStudentId(String studentId);
}
