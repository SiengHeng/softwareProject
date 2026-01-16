package com.university.courseenrollment.demogradle.service.googlesheets;

import com.university.courseenrollment.demogradle.model.entity.Attendance;

import java.util.List;

public interface GoogleSheetsService {
    
    /**
     * Sync a single attendance record to Google Sheets
     */
    void syncAttendanceToSheet(Attendance attendance);
    
    /**
     * Sync multiple attendance records to Google Sheets
     */
    void syncBulkAttendanceToSheet(List<Attendance> attendances);
    
    /**
     * Create a new Google Sheet for a course
     */
    String createCourseAttendanceSheet(Long courseId, String courseName);
    
    /**
     * Get the Google Sheet URL for a course
     */
    String getCourseSheetUrl(Long courseId);
    
    /**
     * Check if Google Sheets integration is enabled
     */
    boolean isEnabled();
    
    /**
     * Sync all unsynced attendance records
     */
    void syncUnsyncedRecords();
}
