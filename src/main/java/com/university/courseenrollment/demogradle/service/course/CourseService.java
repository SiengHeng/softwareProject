package com.university.courseenrollment.demogradle.service.course;

import com.university.courseenrollment.demogradle.dto.CourseDTO;
import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.enums.CourseStatus;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course createCourse(CourseDTO dto);
    Course updateCourse(Long id, CourseDTO dto);
    void deleteCourse(Long id);
    Optional<Course> getCourseById(Long id);
    Optional<Course> getCourseByCourseCode(String courseCode);
    List<Course> getAllCourses();
    List<Course> getCoursesByStatus(CourseStatus status);
    List<Course> getCoursesByLecturer(Long lecturerId);
    List<Course> getCoursesByDepartment(Long departmentId);
    List<Course> getCoursesBySemester(Long semesterId);
    List<Course> getAvailableCourses();
    List<Course> searchCourses(String keyword);
    void updateEnrollmentCount(Long courseId);
    CourseDTO convertToDTO(Course course);
}
