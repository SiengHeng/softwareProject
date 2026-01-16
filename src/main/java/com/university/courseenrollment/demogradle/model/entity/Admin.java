package com.university.courseenrollment.demogradle.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    @Column(unique = true, nullable = false, length = 20)
    private String adminId;

    @Column(nullable = false)
    private Integer adminLevel = 1;

    @Column(length = 100)
    private String department;
}
