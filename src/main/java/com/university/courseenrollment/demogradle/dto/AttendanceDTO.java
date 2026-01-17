package com.university.courseenrollment.demogradle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long scheduleId;
    private Long studentId;
    private LocalDate attendanceDate;
    private String status; // PRESENT, ABSENT, LATE, EXCUSED
    private String notes;
    private String markedBy;
    private Boolean googleSheetSynced;
    
    // Additional fields for display
    private String studentName;
    private String studentNumber;
    private String courseName;
    private String courseCode;
}
