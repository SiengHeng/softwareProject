package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.StudentDTO;
import com.university.courseenrollment.demogradle.model.entity.Department;
import com.university.courseenrollment.demogradle.model.entity.Role;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.repository.DepartmentRepository;
import com.university.courseenrollment.demogradle.repository.RoleRepository;
import com.university.courseenrollment.demogradle.repository.StudentRepository;
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
public class
StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Student createStudent(StudentDTO dto) {
        if (studentRepository.existsByStudentId(dto.getStudentId())) {
            throw new DuplicateResourceException("Student ID already exists: " + dto.getStudentId());
        }

        Student student = new Student();
        student.setUsername(dto.getUsername());
        student.setPassword(passwordEncoder.encode("student123")); // Default password
        student.setEmail(dto.getEmail());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setStudentId(dto.getStudentId());
        student.setMajor(dto.getMajor());
        student.setYearLevel(dto.getYearLevel() != null ? dto.getYearLevel() : 1);
        student.setGpa(dto.getGpa() != null ? dto.getGpa() : 0.0);
        student.setActive(true);

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Department not found"));
            student.setDepartment(department);
        }

        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, StudentDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        student.setEmail(dto.getEmail());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setProfilePicture(dto.getProfilePicture());
        student.setMajor(dto.getMajor());
        student.setYearLevel(dto.getYearLevel());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            student.setDepartment(department);
        }

        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<Student> getStudentsByYearLevel(Integer yearLevel) {
        return studentRepository.findByYearLevel(yearLevel);
    }

    @Override
    @Transactional
    public void updateGPA(Long studentId, Double gpa) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        student.setGpa(gpa);
        studentRepository.save(student);
    }

    @Override
    public StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setUsername(student.getUsername());
        dto.setEmail(student.getEmail());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setProfilePicture(student.getProfilePicture());
        dto.setStudentId(student.getStudentId());
        dto.setMajor(student.getMajor());
        dto.setYearLevel(student.getYearLevel());
        dto.setGpa(student.getGpa());
        dto.setActive(student.isActive());

        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
            dto.setDepartmentName(student.getDepartment().getDepartmentName());
        }

        return dto;
    }
}
