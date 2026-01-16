package com.university.courseenrollment.demogradle.service.attendance;

import com.university.courseenrollment.demogradle.dto.AttendanceDTO;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.model.entity.Attendance;
import com.university.courseenrollment.demogradle.model.entity.Schedule;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.repository.AttendanceRepository;
import com.university.courseenrollment.demogradle.repository.ScheduleRepository;
import com.university.courseenrollment.demogradle.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    
    @Override
    public Attendance markAttendance(AttendanceDTO dto) {
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        // Check if attendance already exists
        Optional<Attendance> existing = attendanceRepository.findByScheduleIdAndStudentIdAndAttendanceDate(
                dto.getScheduleId(), dto.getStudentId(), dto.getAttendanceDate());
        
        if (existing.isPresent()) {
            // Update existing
            Attendance attendance = existing.get();
            attendance.setStatus(dto.getStatus());
            attendance.setNotes(dto.getNotes());
            attendance.setMarkedBy(dto.getMarkedBy());
            return attendanceRepository.save(attendance);
        }
        
        // Create new
        Attendance attendance = new Attendance();
        attendance.setSchedule(schedule);
        attendance.setStudent(student);
        attendance.setAttendanceDate(dto.getAttendanceDate());
        attendance.setStatus(dto.getStatus());
        attendance.setNotes(dto.getNotes());
        attendance.setMarkedBy(dto.getMarkedBy());
        attendance.setGoogleSheetSynced(false);
        
        return attendanceRepository.save(attendance);
    }
    
    @Override
    public Attendance updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        
        attendance.setStatus(dto.getStatus());
        attendance.setNotes(dto.getNotes());
        attendance.setMarkedBy(dto.getMarkedBy());
        attendance.setGoogleSheetSynced(false); // Mark as needing sync
        
        return attendanceRepository.save(attendance);
    }
    
    @Override
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance record not found");
        }
        attendanceRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceBySchedule(Long scheduleId) {
        return attendanceRepository.findByScheduleId(scheduleId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStudent(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByCourse(Long courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByScheduleAndDate(Long scheduleId, LocalDate date) {
        return attendanceRepository.findByScheduleIdAndAttendanceDate(scheduleId, date);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByLecturer(Long lecturerId) {
        return attendanceRepository.findByLecturerId(lecturerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> getAttendanceByScheduleStudentAndDate(Long scheduleId, Long studentId, LocalDate date) {
        return attendanceRepository.findByScheduleIdAndStudentIdAndAttendanceDate(scheduleId, studentId, date);
    }
    
    @Override
    public List<Attendance> markBulkAttendance(List<AttendanceDTO> dtos) {
        return dtos.stream()
                .map(this::markAttendance)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setScheduleId(attendance.getSchedule().getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setStatus(attendance.getStatus());
        dto.setNotes(attendance.getNotes());
        dto.setMarkedBy(attendance.getMarkedBy());
        dto.setGoogleSheetSynced(attendance.getGoogleSheetSynced());
        
        // Additional display fields
        dto.setStudentName(attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName());
        dto.setStudentNumber(attendance.getStudent().getStudentId()); // Student ID is used as student number
        dto.setCourseName(attendance.getSchedule().getCourse().getCourseName());
        dto.setCourseCode(attendance.getSchedule().getCourse().getCourseCode());
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceSummaryForCourse(Long courseId) {
        List<Attendance> attendances = attendanceRepository.findByCourseId(courseId);
        return attendances.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
