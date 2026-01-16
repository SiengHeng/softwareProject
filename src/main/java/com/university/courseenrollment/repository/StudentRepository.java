package com.university.courseenrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.courseenrollment.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
