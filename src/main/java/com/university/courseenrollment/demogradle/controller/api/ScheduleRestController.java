package com.university.courseenrollment.demogradle.controller.api;

import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import com.university.courseenrollment.demogradle.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        var schedules = scheduleService.getAllSchedules().stream()
                .map(scheduleService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ScheduleDTO>> getCourseSchedules(@PathVariable Long courseId) {
        var schedules = scheduleService.getSchedulesByCourse(courseId).stream()
                .map(scheduleService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(schedules);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        try {
            var schedule = scheduleService.createSchedule(scheduleDTO);
            return ResponseEntity.ok(scheduleService.convertToDTO(schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
