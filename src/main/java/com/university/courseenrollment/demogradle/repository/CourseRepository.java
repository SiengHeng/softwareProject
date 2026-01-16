package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.enums.CourseStatus;
import com.university.courseenrollment.demogradle.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByDepartmentId(Long departmentId);
    List<Course> findByLecturerId(Long lecturerId);
    List<Course> findBySemesterId(Long semesterId);
    List<Course> findByStatus(CourseStatus status);
    boolean existsByCourseCode(String courseCode);
    
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.currentEnrolled < c.maxStudents")
    List<Course> findAvailableCourses(@Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCourses(@Param("keyword") String keyword);
}
