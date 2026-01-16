package com.university.courseenrollment.demogradle.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_sheets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false, unique = true)
    private Long courseId;

    @Column(name = "spreadsheet_id", nullable = false)
    private String spreadsheetId;

    @Column(name = "spreadsheet_url", nullable = false)
    private String spreadsheetUrl;

    @Column(name = "sheet_name")
    private String sheetName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "sync_count")
    private Integer syncCount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (syncCount == null) {
            syncCount = 0;
        }
    }
}
