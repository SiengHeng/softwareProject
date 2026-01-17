package com.university.courseenrollment.demogradle.dto;

import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
    private Double grade;
    private String remarks;
}