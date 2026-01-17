package com.university.courseenrollment.demogradle.controller.api;

import com.university.courseenrollment.demogradle.service.googlesheets.GoogleSheetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/googlesheets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
public class GoogleSheetsController {
    
    private final GoogleSheetsService googleSheetsService;
    
    @PostMapping("/create/{courseId}")
    public ResponseEntity<Map<String, String>> createCourseSheet(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> request) {
        
        String courseName = request.get("courseName");
        String sheetUrl = googleSheetsService.createCourseAttendanceSheet(courseId, courseName);
        
        Map<String, String> response = new HashMap<>();
        response.put("sheetUrl", sheetUrl);
        response.put("message", "Google Sheet created successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sync/{courseId}")
    public ResponseEntity<Map<String, String>> syncCourseAttendance(@PathVariable Long courseId) {
        googleSheetsService.syncUnsyncedRecords();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendance synced to Google Sheets successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/url/{courseId}")
    public ResponseEntity<Map<String, String>> getCourseSheetUrl(@PathVariable Long courseId) {
        String sheetUrl = googleSheetsService.getCourseSheetUrl(courseId);
        
        Map<String, String> response = new HashMap<>();
        response.put("sheetUrl", sheetUrl);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getIntegrationStatus() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("enabled", googleSheetsService.isEnabled());
        
        return ResponseEntity.ok(response);
    }
}
