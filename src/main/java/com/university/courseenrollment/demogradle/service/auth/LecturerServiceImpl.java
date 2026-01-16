package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.LecturerDTO;
import com.university.courseenrollment.demogradle.model.entity.Department;
import com.university.courseenrollment.demogradle.model.entity.Lecturer;
import com.university.courseenrollment.demogradle.model.entity.Role;
import com.university.courseenrollment.demogradle.repository.DepartmentRepository;
import com.university.courseenrollment.demogradle.repository.LecturerRepository;
import com.university.courseenrollment.demogradle.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Lecturer createLecturer(LecturerDTO dto) {
        if (lecturerRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + dto.getEmployeeId());
        }

        Lecturer lecturer = new Lecturer();
        lecturer.setUsername(dto.getUsername());
        lecturer.setPassword(passwordEncoder.encode("lecturer123")); // Default password
        lecturer.setEmail(dto.getEmail());
        lecturer.setFirstName(dto.getFirstName());
        lecturer.setLastName(dto.getLastName());
        lecturer.setPhoneNumber(dto.getPhoneNumber());
        lecturer.setEmployeeId(dto.getEmployeeId());
        lecturer.setOfficeRoom(dto.getOfficeRoom());
        lecturer.setSpecialization(dto.getSpecialization());
        lecturer.setActive(true);

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            lecturer.setDepartment(department);
        }

        Role lecturerRole = roleRepository.findByName("ROLE_LECTURER")
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(lecturerRole);
        lecturer.setRoles(roles);

        return lecturerRepository.save(lecturer);
    }

    @Override
    @Transactional
    public Lecturer updateLecturer(Long id, LecturerDTO dto) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found with id: " + id));

        lecturer.setEmail(dto.getEmail());
        lecturer.setFirstName(dto.getFirstName());
        lecturer.setLastName(dto.getLastName());
        lecturer.setPhoneNumber(dto.getPhoneNumber());
        lecturer.setProfilePicture(dto.getProfilePicture());
        lecturer.setOfficeRoom(dto.getOfficeRoom());
        lecturer.setSpecialization(dto.getSpecialization());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            lecturer.setDepartment(department);
        }

        return lecturerRepository.save(lecturer);
    }

    @Override
    @Transactional
    public void deleteLecturer(Long id) {
        if (!lecturerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lecturer not found with id: " + id);
        }
        lecturerRepository.deleteById(id);
    }

    @Override
    public Optional<Lecturer> getLecturerById(Long id) {
        return lecturerRepository.findById(id);
    }

    @Override
    public Optional<Lecturer> getLecturerByEmployeeId(String employeeId) {
        return lecturerRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<Lecturer> getLecturerByUsername(String username) {
        return lecturerRepository.findByUsername(username);
    }

    @Override
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    @Override
    public List<Lecturer> getLecturersByDepartment(Long departmentId) {
        return lecturerRepository.findByDepartmentId(departmentId);
    }

    @Override
    public LecturerDTO convertToDTO(Lecturer lecturer) {
        LecturerDTO dto = new LecturerDTO();
        dto.setId(lecturer.getId());
        dto.setUsername(lecturer.getUsername());
        dto.setEmail(lecturer.getEmail());
        dto.setFirstName(lecturer.getFirstName());
        dto.setLastName(lecturer.getLastName());
        dto.setPhoneNumber(lecturer.getPhoneNumber());
        dto.setProfilePicture(lecturer.getProfilePicture());
        dto.setEmployeeId(lecturer.getEmployeeId());
        dto.setOfficeRoom(lecturer.getOfficeRoom());
        dto.setSpecialization(lecturer.getSpecialization());
        dto.setActive(lecturer.isActive());

        if (lecturer.getDepartment() != null) {
            dto.setDepartmentId(lecturer.getDepartment().getId());
            dto.setDepartmentName(lecturer.getDepartment().getDepartmentName());
        }

        return dto;
    }
}
