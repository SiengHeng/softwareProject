-- Create table to store Google Sheets mapping for courses
CREATE TABLE IF NOT EXISTS course_sheets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL UNIQUE,
    spreadsheet_id VARCHAR(255) NOT NULL,
    spreadsheet_url VARCHAR(512) NOT NULL,
    sheet_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_synced_at TIMESTAMP NULL,
    sync_count INT DEFAULT 0,
    CONSTRAINT fk_course_sheets_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create index on course_id for faster lookups
CREATE INDEX idx_course_sheets_course_id ON course_sheets(course_id);
