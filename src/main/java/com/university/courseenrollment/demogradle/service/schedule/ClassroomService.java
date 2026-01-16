package com.university.courseenrollment.demogradle.service.schedule;

import com.university.courseenrollment.demogradle.dto.ClassroomDTO;
import com.university.courseenrollment.demogradle.model.entity.Classroom;
import java.util.List;
import java.util.Optional;

public interface ClassroomService {
    Classroom createClassroom(ClassroomDTO dto);
    Classroom updateClassroom(Long id, ClassroomDTO dto);
    void deleteClassroom(Long id);
    Optional<Classroom> getClassroomById(Long id);
    Optional<Classroom> getClassroomByRoomNumber(String roomNumber);
    List<Classroom> getAllClassrooms();
    List<Classroom> getClassroomsByBuilding(String building);
    List<Classroom> getAvailableClassrooms();
    ClassroomDTO convertToDTO(Classroom classroom);
}
