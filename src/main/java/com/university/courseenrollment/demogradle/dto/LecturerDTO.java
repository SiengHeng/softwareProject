package com.university.courseenrollment.demogradle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private String employeeId;
    private String officeRoom;
    private String specialization;
    private Long departmentId;
    private String departmentName;
    private boolean active;
}