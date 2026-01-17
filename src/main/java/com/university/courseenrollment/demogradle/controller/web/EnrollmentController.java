package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.repository.DepartmentRepository;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import com.university.courseenrollment.demogradle.service.auth.StudentService;
import com.university.courseenrollment.demogradle.service.course.CourseService;
import com.university.courseenrollment.demogradle.service.course.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enrollment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/list")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String listEnrollments(Model model, @RequestParam(required = false) String status) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Student student = studentService.getStudentByUsername(username).orElseThrow();

        var enrollments = enrollmentService.getEnrollmentsByStudent(student.getId());

        if (status != null && !status.isEmpty()) {
            enrollments = enrollments.stream()
                    .filter(e -> e.getStatus().name().equals(status))
                    .toList();
        }

        // Initialize nested fields used by the templates
        enrollments.forEach(e -> {
            if (e.getCourse() != null) {
                e.getCourse().getCourseName();
                if (e.getCourse().getLecturer() != null) {
                    e.getCourse().getLecturer().getFullName();
                }
            }
        });

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("status", status);

        return "enrollment/enrollment-list";
    }

    @GetMapping("/my-enrollments")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String myEnrollments(Model model, @RequestParam(required = false) String status) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Student student = studentService.getStudentByUsername(username).orElseThrow();

        var enrollments = enrollmentService.getEnrollmentsByStudent(student.getId());

        if (status != null && !status.isEmpty()) {
            enrollments = enrollments.stream()
                    .filter(e -> e.getStatus().name().equals(status))
                    .toList();
        }

        enrollments.forEach(e -> {
            if (e.getCourse() != null) {
                e.getCourse().getCourseName();
                if (e.getCourse().getLecturer() != null) {
                    e.getCourse().getLecturer().getFullName();
                }
            }
        });

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("status", status);
        model.addAttribute("student", student);

        return "enrollment/my-enrollments";
    }

    @GetMapping("/enroll")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String enrollPage(Model model,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) Long departmentId,
                             @RequestParam(required = false) Integer credits) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Student student = studentService.getStudentByUsername(username).orElseThrow();

        var courses = courseService.getAvailableCourses();

        if (search != null && !search.isEmpty()) {
            courses = courseService.searchCourses(search);
        }

        // Build a set of course IDs the student is already enrolled in (any status)
        java.util.Set<Long> enrolledCourseIds = enrollmentService.getEnrollmentsByStudent(student.getId()).stream()
                .filter(e -> e.getCourse() != null)
                .map(e -> e.getCourse().getId())
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        // Eagerly initialize lazy-loaded relationships to avoid LazyInitializationException
        courses.forEach(course -> {
            if (course.getLecturer() != null) {
                course.getLecturer().getFullName(); // Force initialization
            }
            if (course.getDepartment() != null) {
                course.getDepartment().getDepartmentName(); // Force initialization
            }
            // Initialize schedules if present
            if (course.getSchedules() != null) {
                course.getSchedules().size(); // Force initialization
            }
        });

        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        model.addAttribute("availableCourses", courses);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("search", search);
        model.addAttribute("departmentId", departmentId);
        model.addAttribute("credits", credits);

        return "enrollment/enroll-course";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam Long courseId, RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername().orElseThrow();
            Student student = studentService.getStudentByUsername(username).orElseThrow();

            enrollmentService.enrollStudent(student.getId(), courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment request submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/enrollment/list";
    }

    @PostMapping("/drop/{id}")
    public String dropEnrollment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.dropEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course dropped successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/enrollment/list";
    }
}
