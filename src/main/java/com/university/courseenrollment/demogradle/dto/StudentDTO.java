package com.university.courseenrollment.demogradle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private String studentId;
    private String major;
    private Integer yearLevel;
    private Double gpa;
    private Long departmentId;
    private String departmentName;
    private boolean active;
}
