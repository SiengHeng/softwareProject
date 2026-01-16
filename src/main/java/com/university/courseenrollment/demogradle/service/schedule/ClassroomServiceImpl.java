package com.university.courseenrollment.demogradle.service.schedule;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.ClassroomDTO;
import com.university.courseenrollment.demogradle.model.entity.Classroom;
import com.university.courseenrollment.demogradle.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;

    @Override
    @Transactional
    public Classroom createClassroom(ClassroomDTO dto) {
        if (classroomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists");
        }

        Classroom classroom = new Classroom();
        classroom.setRoomNumber(dto.getRoomNumber());
        classroom.setBuilding(dto.getBuilding());
        classroom.setCapacity(dto.getCapacity());
        classroom.setRoomType(dto.getRoomType());
        classroom.setHasProjector(dto.isHasProjector());
        classroom.setHasComputers(dto.isHasComputers());
        classroom.setAvailable(dto.isAvailable());

        return classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public Classroom updateClassroom(Long id, ClassroomDTO dto) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found"));

        classroom.setBuilding(dto.getBuilding());
        classroom.setCapacity(dto.getCapacity());
        classroom.setRoomType(dto.getRoomType());
        classroom.setHasProjector(dto.isHasProjector());
        classroom.setHasComputers(dto.isHasComputers());
        classroom.setAvailable(dto.isAvailable());

        return classroomRepository.save(classroom);
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classroom not found");
        }
        classroomRepository.deleteById(id);
    }

    @Override
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    @Override
    public Optional<Classroom> getClassroomByRoomNumber(String roomNumber) {
        return classroomRepository.findByRoomNumber(roomNumber);
    }

    @Override
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    @Override
    public List<Classroom> getClassroomsByBuilding(String building) {
        return classroomRepository.findByBuilding(building);
    }

    @Override
    public List<Classroom> getAvailableClassrooms() {
        return classroomRepository.findByIsAvailable(true);
    }

    @Override
    public ClassroomDTO convertToDTO(Classroom classroom) {
        ClassroomDTO dto = new ClassroomDTO();
        dto.setId(classroom.getId());
        dto.setRoomNumber(classroom.getRoomNumber());
        dto.setBuilding(classroom.getBuilding());
        dto.setCapacity(classroom.getCapacity());
        dto.setRoomType(classroom.getRoomType());
        dto.setHasProjector(classroom.isHasProjector());
        dto.setHasComputers(classroom.isHasComputers());
        dto.setAvailable(classroom.isAvailable());
        return dto;
    }
}
