package com.university.courseenrollment.demogradle.dto;

import com.university.courseenrollment.demogradle.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long classroomId;
    private String classroomNumber;
    private String building;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}