package com.example.demo.service;

import com.example.demo.model.Enrollment;
import com.example.demo.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository repository;

    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public void enroll(Long studentId, Long courseId) {
        repository.save(new Enrollment(studentId, courseId));
    }

    public void drop(Long studentId, Long courseId) {
        repository.deleteByStudentIdAndCourseId(studentId, courseId);
    }

    public List<Enrollment> getEnrollments(Long studentId) {
        return repository.findByStudentId(studentId);
    }
}
