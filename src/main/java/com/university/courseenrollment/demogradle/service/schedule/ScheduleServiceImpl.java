package com.university.courseenrollment.demogradle.service.schedule;

import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.exception.ScheduleConflictException;
import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import com.university.courseenrollment.demogradle.model.entity.Classroom;
import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.model.entity.Schedule;
import com.university.courseenrollment.demogradle.enums.DayOfWeek;
import com.university.courseenrollment.demogradle.repository.ClassroomRepository;
import com.university.courseenrollment.demogradle.repository.CourseRepository;
import com.university.courseenrollment.demogradle.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;
    private final ConflictDetectionService conflictDetectionService;

    @Override
    @Transactional
    public Schedule createSchedule(ScheduleDTO dto) {
        if (conflictDetectionService.hasScheduleConflict(dto)) {
            throw new ScheduleConflictException("Schedule conflict detected");
        }

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));

        Schedule schedule = new Schedule();
        schedule.setCourse(course);
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public Schedule updateSchedule(Long id, ScheduleDTO dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Schedule not found"));

        dto.setId(id); // For conflict check excluding self
        if (conflictDetectionService.hasScheduleConflict(dto)) {
            throw new ScheduleConflictException("Schedule conflict detected");
        }

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found"));
        Classroom classroom = classroomRepository.findById(dto.getClassroomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Classroom not found"));

        schedule.setCourse(course);
        schedule.setClassroom(classroom);
        schedule.setDayOfWeek(dto.getDayOfWeek());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule not found");
        }
        scheduleRepository.deleteById(id);
    }

    @Override
    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public List<Schedule> getAllSchedules() {

        return scheduleRepository.findAll();
    }

    @Override
    public List<Schedule> getSchedulesByCourse(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }

    @Override
    public List<Schedule> getSchedulesByClassroom(Long classroomId) {
        return scheduleRepository.findByClassroomId(classroomId);
    }

    @Override
    public List<Schedule> getSchedulesByDay(DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDayOfWeek(dayOfWeek);
    }

    @Override
    public boolean hasConflict(ScheduleDTO dto) {
        return conflictDetectionService.hasScheduleConflict(dto);
    }

    @Override
    public ScheduleDTO convertToDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setCourseId(schedule.getCourse().getId());
        dto.setCourseCode(schedule.getCourse().getCourseCode());
        dto.setCourseName(schedule.getCourse().getCourseName());
        dto.setClassroomId(schedule.getClassroom().getId());
        dto.setClassroomNumber(schedule.getClassroom().getRoomNumber());
        dto.setBuilding(schedule.getClassroom().getBuilding());
        dto.setDayOfWeek(schedule.getDayOfWeek());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        return dto;
    }
}
