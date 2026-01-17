package com.university.courseenrollment.demogradle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private Long id;
    private String roomNumber;
    private String building;
    private Integer capacity;
    private String roomType;
    private boolean hasProjector;
    private boolean hasComputers;
    private boolean isAvailable;
}