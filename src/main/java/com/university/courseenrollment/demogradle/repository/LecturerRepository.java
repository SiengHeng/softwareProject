package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    Optional<Lecturer> findByEmployeeId(String employeeId);
    Optional<Lecturer> findByUsername(String username);
    List<Lecturer> findByDepartmentId(Long departmentId);
    boolean existsByEmployeeId(String employeeId);
}
