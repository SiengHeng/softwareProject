package com.university.courseenrollment.demogradle.service.course;

import com.university.courseenrollment.demogradle.dto.EnrollmentDTO;
import com.university.courseenrollment.demogradle.model.entity.Enrollment;
import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    Enrollment enrollStudent(Long studentId, Long courseId);
    Enrollment updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status);
    Enrollment updateGrade(Long enrollmentId, Double grade);
    void dropEnrollment(Long enrollmentId);
    Optional<Enrollment> getEnrollmentById(Long id);
    List<Enrollment> getEnrollmentsByStudent(Long studentId);
    List<Enrollment> getEnrollmentsByCourse(Long courseId);
    List<Enrollment> getEnrollmentsByStatus(EnrollmentStatus status);
    List<Enrollment> getEnrollmentsByStatusWithDetails(EnrollmentStatus status);
    List<Enrollment> getAllEnrollments();
    boolean isStudentEnrolled(Long studentId, Long courseId);
    Enrollment approveEnrollment(Long enrollmentId);
    Enrollment rejectEnrollment(Long enrollmentId);
    EnrollmentDTO convertToDTO(Enrollment enrollment);
}
