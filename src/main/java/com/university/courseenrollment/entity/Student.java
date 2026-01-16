package com.university.courseenrollment.entity;

import java.util.List;

import jakarta.persistence.Entity;

@Entity
public class Student extends User {

    // No fields needed here, as name and email are in User, and enrollments is for JPA entity
    // If specific fields for Student DTO are needed, they should be added here.

    public Student() {
    }

    // If specific constructor is needed, it should be added here.

}
