package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import com.university.courseenrollment.demogradle.model.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course WHERE e.status = :status")
    List<Enrollment> findByStatusWithStudentAndCourse(@Param("status") EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student s JOIN FETCH e.course c LEFT JOIN FETCH c.lecturer")
    List<Enrollment> findAllWithStudentAndCourse();
    
    @Query("SELECT e FROM Enrollment e " +
           "JOIN FETCH e.course c " +
           "LEFT JOIN FETCH c.lecturer " +
           "WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentIdWithCourseAndLecturer(@Param("studentId") Long studentId);
    
    List<Enrollment> findByStatus(EnrollmentStatus status);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
}
