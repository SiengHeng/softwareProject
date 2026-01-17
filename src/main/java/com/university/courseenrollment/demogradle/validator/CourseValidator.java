package com.university.courseenrollment.demogradle.validator;

import com.university.courseenrollment.demogradle.dto.CourseDTO;
import org.springframework.stereotype.Component;

@Component
public class
CourseValidator {

    public boolean validate(CourseDTO dto) {
        if (dto.getCredits() < 1 || dto.getCredits() > 6) {
            return false;
        }
        if (dto.getMaxStudents() < 1) {
            return false;
        }
        return dto.getCourseCode() != null && !dto.getCourseCode().trim().isEmpty();
    }
}