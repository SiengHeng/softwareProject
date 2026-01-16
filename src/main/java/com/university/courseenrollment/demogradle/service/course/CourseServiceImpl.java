package com.university.courseenrollment.demogradle.service.course;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.CourseDTO;
import com.university.courseenrollment.demogradle.model.entity.Course;
import com.university.courseenrollment.demogradle.model.entity.Department;
import com.university.courseenrollment.demogradle.model.entity.Lecturer;
import com.university.courseenrollment.demogradle.model.entity.Semester;
import com.university.courseenrollment.demogradle.enums.CourseStatus;
import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import com.university.courseenrollment.demogradle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public Course createCourse(CourseDTO dto) {
        if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + dto.getCourseCode());
        }

        Course course = new Course();
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        course.setMaxStudents(dto.getMaxStudents());
        course.setCurrentEnrolled(0);
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : CourseStatus.ACTIVE);

        if (dto.getLecturerId() != null) {
            Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
            course.setLecturer(lecturer);
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            course.setDepartment(department);
        }

        if (dto.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
            course.setSemester(semester);
        }

        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        course.setMaxStudents(dto.getMaxStudents());
        course.setStatus(dto.getStatus());

        if (dto.getLecturerId() != null) {
            Lecturer lecturer = lecturerRepository.findById(dto.getLecturerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lecturer not found"));
            course.setLecturer(lecturer);
        }

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            course.setDepartment(department);
        }

        if (dto.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester not found"));
            course.setSemester(semester);
        }

        return courseRepository.save(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Optional<Course> getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatus(status);
    }

    @Override
    public List<Course> getCoursesByLecturer(Long lecturerId) {
        return courseRepository.findByLecturerId(lecturerId);
    }

    @Override
    public List<Course> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<Course> getCoursesBySemester(Long semesterId) {
        return courseRepository.findBySemesterId(semesterId);
    }

    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses(CourseStatus.ACTIVE);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findAll().stream()
                .filter(course -> course.getCourseCode().toLowerCase().contains(keyword.toLowerCase())
                        || course.getCourseName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    @Transactional
    public void updateEnrollmentCount(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        long approvedCount = enrollmentRepository.countByCourseIdAndStatus(courseId, EnrollmentStatus.APPROVED);
        course.setCurrentEnrolled((int) approvedCount);

        if (course.getCurrentEnrolled() >= course.getMaxStudents()) {
            course.setStatus(CourseStatus.FULL);
        } else if (course.getStatus() == CourseStatus.FULL) {
            course.setStatus(CourseStatus.ACTIVE);
        }

        courseRepository.save(course);
    }

    @Override
    public CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        dto.setMaxStudents(course.getMaxStudents());
        dto.setCurrentEnrolled(course.getCurrentEnrolled());
        dto.setStatus(course.getStatus());

        if (course.getLecturer() != null) {
            dto.setLecturerId(course.getLecturer().getId());
            dto.setLecturerName(course.getLecturer().getFullName());
        }

        if (course.getDepartment() != null) {
            dto.setDepartmentId(course.getDepartment().getId());
            dto.setDepartmentName(course.getDepartment().getDepartmentName());
        }

        if (course.getSemester() != null) {
            dto.setSemesterId(course.getSemester().getId());
            dto.setSemesterName(course.getSemester().getFullName());
        }

        return dto;
    }
}
