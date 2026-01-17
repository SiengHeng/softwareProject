-- Create attendance table
CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    marked_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    google_sheet_synced BOOLEAN DEFAULT FALSE,
    google_sheet_row_id VARCHAR(100),
    
    CONSTRAINT fk_attendance_schedule FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    CONSTRAINT uk_attendance_unique UNIQUE (schedule_id, student_id, attendance_date),
    INDEX idx_attendance_schedule (schedule_id),
    INDEX idx_attendance_student (student_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_attendance_status (status),
    INDEX idx_attendance_synced (google_sheet_synced)
);

-- Add comment
ALTER TABLE attendance COMMENT = 'Stores student attendance records for scheduled classes';
