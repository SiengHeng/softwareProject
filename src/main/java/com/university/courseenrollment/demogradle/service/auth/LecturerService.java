package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.dto.LecturerDTO;
import com.university.courseenrollment.demogradle.model.entity.Lecturer;
import java.util.List;
import java.util.Optional;
public interface LecturerService {
    Lecturer createLecturer(LecturerDTO dto);
    Lecturer updateLecturer(Long id, LecturerDTO dto);
    void deleteLecturer(Long id);
    Optional<Lecturer> getLecturerById(Long id);
    Optional<Lecturer> getLecturerByEmployeeId(String employeeId);
    Optional<Lecturer> getLecturerByUsername(String username);
    List<Lecturer> getAllLecturers();
    List<Lecturer> getLecturersByDepartment(Long departmentId);
    LecturerDTO convertToDTO(Lecturer lecturer);
}