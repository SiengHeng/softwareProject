package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.CourseSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseSheetRepository extends JpaRepository<CourseSheet, Long> {
    Optional<CourseSheet> findByCourseId(Long courseId);
    boolean existsByCourseId(Long courseId);
}
