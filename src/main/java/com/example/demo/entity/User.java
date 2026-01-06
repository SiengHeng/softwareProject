package com.example.demo.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;

public class User {

    private UUID id;

    @Column(unique = true)
    private String username;

    private String name;
    private String email;

    @Column(nullable = false)
    private String type;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // Getters and Setters ...
}
