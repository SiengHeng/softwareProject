package com.university.courseenrollment.demogradle.validator;

import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.model.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentValidator {

    public boolean canEnroll(Student student, Course course) {
        if (!course.getStatus().name().equals("ACTIVE")) {
            return false;
        }
        if (course.isFull()) {
            return false;
        }
        return student.isActive();
    }
}