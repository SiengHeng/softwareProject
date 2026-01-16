package com.university.courseenrollment.demogradle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.courseenrollment.demogradle.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
