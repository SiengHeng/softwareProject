package com.university.courseenrollment.demogradle.service.course;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.EnrollmentDTO;
import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.model.entity.Enrollment;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import com.university.courseenrollment.demogradle.repository.CourseRepository;
import com.university.courseenrollment.demogradle.repository.EnrollmentRepository;
import com.university.courseenrollment.demogradle.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    @Override
    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("Student already enrolled in this course");
        }

        if (course.isFull()) {
            throw new RuntimeException("Course is full");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.PENDING);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        courseService.updateEnrollmentCount(courseId);

        return savedEnrollment;
    }

    @Override
    @Transactional
    public Enrollment updateEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollment.setStatus(status);

        if (status == EnrollmentStatus.APPROVED && enrollment.getEnrolledAt() == null) {
            enrollment.setEnrolledAt(LocalDateTime.now());
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        courseService.updateEnrollmentCount(enrollment.getCourse().getId());

        return savedEnrollment;
    }

    @Override
    @Transactional
    public Enrollment updateGrade(Long enrollmentId, Double grade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollment.setGrade(grade);
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
        courseService.updateEnrollmentCount(enrollment.getCourse().getId());
    }

    @Override
    public Optional<Enrollment> getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdWithCourseAndLecturer(studentId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStatus(EnrollmentStatus status) {
        return enrollmentRepository.findByStatus(status);
    }

    @Override
    public List<Enrollment> getEnrollmentsByStatusWithDetails(EnrollmentStatus status) {
        return enrollmentRepository.findByStatusWithStudentAndCourse(status);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithStudentAndCourse();
    }

    @Override
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional
    public Enrollment approveEnrollment(Long enrollmentId) {
        return updateEnrollmentStatus(enrollmentId, EnrollmentStatus.APPROVED);
    }

    @Override
    @Transactional
    public Enrollment rejectEnrollment(Long enrollmentId) {
        return updateEnrollmentStatus(enrollmentId, EnrollmentStatus.REJECTED);
    }

    @Override
    public EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setStudentName(enrollment.getStudent().getFullName());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        dto.setStatus(enrollment.getStatus());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setGrade(enrollment.getGrade());
        dto.setRemarks(enrollment.getRemarks());
        return dto;
    }
}