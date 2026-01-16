package com.university.courseenrollment.demogradle.service.schedule;

import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import com.university.courseenrollment.demogradle.model.entity.Schedule;
import com.university.courseenrollment.demogradle.enums.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    Schedule createSchedule(ScheduleDTO dto);
    Schedule updateSchedule(Long id, ScheduleDTO dto);
    void deleteSchedule(Long id);
    Optional<Schedule> getScheduleById(Long id);
    List<Schedule> getAllSchedules();
    List<Schedule> getSchedulesByCourse(Long courseId);
    List<Schedule> getSchedulesByClassroom(Long classroomId);
    List<Schedule> getSchedulesByDay(DayOfWeek dayOfWeek);
    boolean hasConflict(ScheduleDTO dto);
    ScheduleDTO convertToDTO(Schedule schedule);
}

