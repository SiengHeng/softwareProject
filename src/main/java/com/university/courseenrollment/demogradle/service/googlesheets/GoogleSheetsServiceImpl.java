package com.university.courseenrollment.demogradle.service.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.university.courseenrollment.demogradle.model.entity.Attendance;
import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.model.entity.CourseSheet;
import com.university.courseenrollment.demogradle.repository.AttendanceRepository;
import com.university.courseenrollment.demogradle.repository.CourseRepository;
import com.university.courseenrollment.demogradle.repository.CourseSheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoogleSheetsServiceImpl implements GoogleSheetsService {
    
    private final AttendanceRepository attendanceRepository;
    private final CourseSheetRepository courseSheetRepository;
    private final CourseRepository courseRepository;
    private final Sheets sheetsService;
    
    @Value("${google.sheets.enabled:false}")
    private boolean googleSheetsEnabled;
    
    public GoogleSheetsServiceImpl(
            AttendanceRepository attendanceRepository,
            CourseSheetRepository courseSheetRepository,
            CourseRepository courseRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) Sheets sheetsService) {
        this.attendanceRepository = attendanceRepository;
        this.courseSheetRepository = courseSheetRepository;
        this.courseRepository = courseRepository;
        this.sheetsService = sheetsService;
    }
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    @Transactional
    public void syncAttendanceToSheet(Attendance attendance) {
        if (!isEnabled()) {
            log.warn("Google Sheets integration is disabled. Skipping sync.");
            return;
        }
        
        try {
            log.info("Syncing attendance record {} to Google Sheets", attendance.getId());
            
            Long courseId = attendance.getSchedule().getCourse().getId();
            CourseSheet courseSheet = courseSheetRepository.findByCourseId(courseId)
                    .orElseThrow(() -> new RuntimeException("No Google Sheet found for course " + courseId));
            
            // Prepare row data
            List<Object> rowData = Arrays.asList(
                attendance.getAttendanceDate().format(DATE_FORMATTER),
                attendance.getStudent().getFirstName() + " " + attendance.getStudent().getLastName(),
                attendance.getStudent().getStudentId(),
                attendance.getStatus().toString(),
                attendance.getNotes() != null ? attendance.getNotes() : ""
            );
            
            // Check if this attendance already has a row
            if (attendance.getGoogleSheetRowId() != null && !attendance.getGoogleSheetRowId().isEmpty()) {
                // Update existing row
                updateRow(courseSheet.getSpreadsheetId(), attendance.getGoogleSheetRowId(), rowData);
            } else {
                // Append new row
                String rowId = appendRow(courseSheet.getSpreadsheetId(), rowData);
                attendance.setGoogleSheetRowId(rowId);
            }
            
            attendance.setGoogleSheetSynced(true);
            attendanceRepository.save(attendance);
            
            // Update sync timestamp
            courseSheet.setLastSyncedAt(LocalDateTime.now());
            courseSheet.setSyncCount(courseSheet.getSyncCount() + 1);
            courseSheetRepository.save(courseSheet);
            
            log.info("Successfully synced attendance record {} to Google Sheets", attendance.getId());
        } catch (Exception e) {
            log.error("Failed to sync attendance record {} to Google Sheets", attendance.getId(), e);
            throw new RuntimeException("Failed to sync to Google Sheets: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void syncBulkAttendanceToSheet(List<Attendance> attendances) {
        if (!isEnabled()) {
            log.warn("Google Sheets integration is disabled. Skipping bulk sync.");
            return;
        }
        
        log.info("Syncing {} attendance records to Google Sheets", attendances.size());
        
        // Group by course for efficient batch operations
        Map<Long, List<Attendance>> byCourse = attendances.stream()
                .collect(Collectors.groupingBy(a -> a.getSchedule().getCourse().getId()));
        
        for (Map.Entry<Long, List<Attendance>> entry : byCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<Attendance> courseAttendances = entry.getValue();
            
            try {
                CourseSheet courseSheet = courseSheetRepository.findByCourseId(courseId)
                        .orElseThrow(() -> new RuntimeException("No Google Sheet found for course " + courseId));
                
                // Prepare all rows
                List<List<Object>> rowsData = courseAttendances.stream()
                        .map(a -> {
                            List<Object> row = new ArrayList<>();
                            row.add(a.getAttendanceDate().format(DATE_FORMATTER));
                            row.add(a.getStudent().getFirstName() + " " + a.getStudent().getLastName());
                            row.add(a.getStudent().getStudentId());
                            row.add(a.getStatus().toString());
                            row.add(a.getNotes() != null ? a.getNotes() : "");
                            return row;
                        })
                        .collect(Collectors.toList());
                
                // Batch append
                appendRows(courseSheet.getSpreadsheetId(), rowsData);
                
                // Mark all as synced
                for (int i = 0; i < courseAttendances.size(); i++) {
                    Attendance attendance = courseAttendances.get(i);
                    attendance.setGoogleSheetSynced(true);
                    attendance.setGoogleSheetRowId("ROW_" + (i + 2)); // Starting from row 2 (after header)
                }
                attendanceRepository.saveAll(courseAttendances);
                
                // Update sync timestamp
                courseSheet.setLastSyncedAt(LocalDateTime.now());
                courseSheet.setSyncCount(courseSheet.getSyncCount() + courseAttendances.size());
                courseSheetRepository.save(courseSheet);
                
            } catch (Exception e) {
                log.error("Failed to bulk sync for course {}", courseId, e);
            }
        }
    }
    
    @Override
    @Transactional
    public String createCourseAttendanceSheet(Long courseId, String courseName) {
        if (!isEnabled()) {
            log.warn("Google Sheets integration is disabled. Cannot create sheet.");
            return null;
        }
        
        try {
            // Check if sheet already exists
            Optional<CourseSheet> existing = courseSheetRepository.findByCourseId(courseId);
            if (existing.isPresent()) {
                log.info("Google Sheet already exists for course {}: {}", courseName, existing.get().getSpreadsheetUrl());
                return existing.get().getSpreadsheetUrl();
            }
            
            log.info("Creating Google Sheet for course: {}", courseName);
            
            // Create new spreadsheet
            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle("Attendance - " + courseName)
                            .setTimeZone("UTC"));
            
            spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();
            String spreadsheetId = spreadsheet.getSpreadsheetId();
            String spreadsheetUrl = spreadsheet.getSpreadsheetUrl();
            
            log.info("Created spreadsheet with ID: {}", spreadsheetId);
            
            // Set up header row
            List<Object> headers = Arrays.asList("Date/Time", "Student Name", "Student Number", "Status", "Notes");
            ValueRange headerRange = new ValueRange()
                    .setValues(Collections.singletonList(headers));
            
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, "A1:E1", headerRange)
                    .setValueInputOption("RAW")
                    .execute();
            
            // Format header row (bold, background color)
            List<Request> requests = new ArrayList<>();
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(0)
                                    .setStartRowIndex(0)
                                    .setEndRowIndex(1))
                            .setCell(new CellData()
                                    .setUserEnteredFormat(new CellFormat()
                                            .setBackgroundColor(new Color()
                                                    .setRed(0.2f)
                                                    .setGreen(0.6f)
                                                    .setBlue(0.9f))
                                            .setTextFormat(new TextFormat()
                                                    .setBold(true)
                                                    .setForegroundColor(new Color()
                                                            .setRed(1f)
                                                            .setGreen(1f)
                                                            .setBlue(1f)))))
                            .setFields("userEnteredFormat(backgroundColor,textFormat)")));
            
            BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
            
            // Save to database
            CourseSheet courseSheet = new CourseSheet();
            courseSheet.setCourseId(courseId);
            courseSheet.setSpreadsheetId(spreadsheetId);
            courseSheet.setSpreadsheetUrl(spreadsheetUrl);
            courseSheet.setSheetName("Sheet1");
            courseSheetRepository.save(courseSheet);
            
            log.info("Created Google Sheet for course {}: {}", courseName, spreadsheetUrl);
            return spreadsheetUrl;
            
        } catch (Exception e) {
            log.error("Failed to create Google Sheet for course: {}", courseName, e);
            throw new RuntimeException("Failed to create Google Sheet: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getCourseSheetUrl(Long courseId) {
        return courseSheetRepository.findByCourseId(courseId)
                .map(CourseSheet::getSpreadsheetUrl)
                .orElse(null);
    }
    
    @Override
    public boolean isEnabled() {
        return googleSheetsEnabled && sheetsService != null;
    }
    
    @Override
    @Transactional
    public void syncUnsyncedRecords() {
        if (!isEnabled()) {
            log.warn("Google Sheets integration is disabled. Skipping unsynced records sync.");
            return;
        }
        
        List<Attendance> unsyncedRecords = attendanceRepository.findUnsyncedRecords();
        log.info("Found {} unsynced attendance records", unsyncedRecords.size());
        
        syncBulkAttendanceToSheet(unsyncedRecords);
    }
    
    // Helper methods
    
    private String appendRow(String spreadsheetId, List<Object> rowData) throws IOException {
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));
        AppendValuesResponse response = sheetsService.spreadsheets().values()
                .append(spreadsheetId, "A:E", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
        
        // Extract row number from the update range
        String updatedRange = response.getUpdates().getUpdatedRange();
        return updatedRange.split("!")[1].split(":")[0]; // e.g., "A2" from "Sheet1!A2:E2"
    }
    
    private void appendRows(String spreadsheetId, List<List<Object>> rowsData) throws IOException {
        ValueRange body = new ValueRange().setValues(rowsData);
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, "A:E", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();
    }
    
    private void updateRow(String spreadsheetId, String rowId, List<Object> rowData) throws IOException {
        String range = rowId + ":" + rowId.replaceAll("[0-9]", "") + rowId.replaceAll("[A-Z]", "");
        ValueRange body = new ValueRange().setValues(Collections.singletonList(rowData));
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
