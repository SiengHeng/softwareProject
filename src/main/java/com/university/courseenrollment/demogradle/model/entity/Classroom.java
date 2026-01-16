package com.university.courseenrollment.demogradle.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String roomNumber;

    @Column(nullable = false, length = 100)
    private String building;

    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 50)
    private String roomType;

    @Column(nullable = false)
    private boolean hasProjector = false;

    @Column(nullable = false)
    private boolean hasComputers = false;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    public String getFullRoomName() {
        return building + " - " + roomNumber;
    }
}
