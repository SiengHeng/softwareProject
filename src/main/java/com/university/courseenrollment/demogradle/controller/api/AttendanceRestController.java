package com.university.courseenrollment.demogradle.controller.api;

import com.university.courseenrollment.demogradle.dto.AttendanceDTO;
import com.university.courseenrollment.demogradle.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
public class AttendanceRestController {
    
    private final AttendanceService attendanceService;
    
    @PostMapping
    public ResponseEntity<AttendanceDTO> markAttendance(@RequestBody AttendanceDTO dto) {
        var attendance = attendanceService.markAttendance(dto);
        return ResponseEntity.ok(attendanceService.convertToDTO(attendance));
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceDTO>> markBulkAttendance(@RequestBody List<AttendanceDTO> dtos) {
        var attendances = attendanceService.markBulkAttendance(dtos);
        var dtoList = attendances.stream()
                .map(attendanceService::convertToDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Long id, @RequestBody AttendanceDTO dto) {
        var attendance = attendanceService.updateAttendance(id, dto);
        return ResponseEntity.ok(attendanceService.convertToDTO(attendance));
    }
    
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceBySchedule(@PathVariable Long scheduleId) {
        var attendances = attendanceService.getAttendanceBySchedule(scheduleId);
        var dtos = attendances.stream()
                .map(attendanceService::convertToDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/schedule/{scheduleId}/date/{date}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByScheduleAndDate(
            @PathVariable Long scheduleId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var attendances = attendanceService.getAttendanceByScheduleAndDate(scheduleId, date);
        var dtos = attendances.stream()
                .map(attendanceService::convertToDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByCourse(@PathVariable Long courseId) {
        var dtos = attendanceService.getAttendanceSummaryForCourse(courseId);
        return ResponseEntity.ok(dtos);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
