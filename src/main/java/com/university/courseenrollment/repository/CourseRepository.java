package com.university.courseenrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.courseenrollment.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
