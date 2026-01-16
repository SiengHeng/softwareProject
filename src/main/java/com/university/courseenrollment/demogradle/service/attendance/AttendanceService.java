package com.university.courseenrollment.demogradle.service.attendance;

import com.university.courseenrollment.demogradle.dto.AttendanceDTO;
import com.university.courseenrollment.demogradle.model.entity.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    
    Attendance markAttendance(AttendanceDTO dto);
    
    Attendance updateAttendance(Long id, AttendanceDTO dto);
    
    void deleteAttendance(Long id);
    
    Optional<Attendance> getAttendanceById(Long id);
    
    List<Attendance> getAttendanceBySchedule(Long scheduleId);
    
    List<Attendance> getAttendanceByStudent(Long studentId);
    
    List<Attendance> getAttendanceByCourse(Long courseId);
    
    List<Attendance> getAttendanceByScheduleAndDate(Long scheduleId, LocalDate date);
    
    List<Attendance> getAttendanceByLecturer(Long lecturerId);
    
    Optional<Attendance> getAttendanceByScheduleStudentAndDate(Long scheduleId, Long studentId, LocalDate date);
    
    List<Attendance> markBulkAttendance(List<AttendanceDTO> dtos);
    
    AttendanceDTO convertToDTO(Attendance attendance);
    
    List<AttendanceDTO> getAttendanceSummaryForCourse(Long courseId);
}
