package com.university.courseenrollment.demogradle.dto;

import com.university.courseenrollment.demogradle.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer credits;
    private Integer maxStudents;
    private Integer currentEnrolled;
    private CourseStatus status;
    private Long lecturerId;
    private String lecturerName;
    private Long departmentId;
    private String departmentName;
    private Long semesterId;
    private String semesterName;
}