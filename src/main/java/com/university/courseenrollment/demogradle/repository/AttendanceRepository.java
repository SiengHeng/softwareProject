package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByScheduleId(Long scheduleId);
    
    List<Attendance> findByStudentId(Long studentId);
    
    List<Attendance> findByScheduleIdAndAttendanceDate(Long scheduleId, LocalDate date);
    
    Optional<Attendance> findByScheduleIdAndStudentIdAndAttendanceDate(
        Long scheduleId, Long studentId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.schedule.course.id = :courseId")
    List<Attendance> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Attendance a WHERE a.schedule.course.id = :courseId AND a.attendanceDate = :date")
    List<Attendance> findByCourseIdAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.schedule.course.lecturer.id = :lecturerId")
    List<Attendance> findByLecturerId(@Param("lecturerId") Long lecturerId);
    
    @Query("SELECT a FROM Attendance a WHERE a.googleSheetSynced = false")
    List<Attendance> findUnsyncedRecords();
}
