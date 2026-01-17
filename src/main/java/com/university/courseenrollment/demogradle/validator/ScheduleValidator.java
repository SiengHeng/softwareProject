package com.university.courseenrollment.demogradle.validator;

import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import org.springframework.stereotype.Component;

@Component
public class  ScheduleValidator {

    public boolean validate(ScheduleDTO dto) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return false;
        }
        return !dto.getStartTime().isAfter(dto.getEndTime());
    }
}
