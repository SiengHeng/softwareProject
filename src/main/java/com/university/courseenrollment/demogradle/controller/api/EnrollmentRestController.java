package com.university.courseenrollment.demogradle.controller.api;

import com.university.courseenrollment.demogradle.dto.EnrollmentDTO;
import com.university.courseenrollment.demogradle.service.course.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getStudentEnrollments(@PathVariable Long studentId) {
        var enrollments = enrollmentService.getEnrollmentsByStudent(studentId).stream()
                .map(enrollmentService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getCourseEnrollments(@PathVariable Long courseId) {
        var enrollments = enrollmentService.getEnrollmentsByCourse(courseId).stream()
                .map(enrollmentService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enroll(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            var enrollment = enrollmentService.enrollStudent(studentId, courseId);
            return ResponseEntity.ok(enrollmentService.convertToDTO(enrollment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> dropEnrollment(@PathVariable Long id) {
        try {
            enrollmentService.dropEnrollment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
