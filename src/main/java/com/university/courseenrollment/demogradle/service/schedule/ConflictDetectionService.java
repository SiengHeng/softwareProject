package com.university.courseenrollment.demogradle.service.schedule;

import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import com.university.courseenrollment.demogradle.model.entity.Schedule;
import com.university.courseenrollment.demogradle.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConflictDetectionService {
    private final ScheduleRepository scheduleRepository;

    public boolean hasScheduleConflict(ScheduleDTO dto) {
        List<Schedule> conflictingSchedules = scheduleRepository.findConflictingSchedules(
                dto.getClassroomId(),
                dto.getDayOfWeek(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        // Exclude the schedule being updated from conflict check
        if (dto.getId() != null) {
            conflictingSchedules = conflictingSchedules.stream()
                    .filter(s -> !s.getId().equals(dto.getId()))
                    .toList();
        }

        return !conflictingSchedules.isEmpty();
    }
}
