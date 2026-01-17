package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.dto.CourseDTO;
import com.university.courseenrollment.demogradle.repository.DepartmentRepository;
import com.university.courseenrollment.demogradle.repository.SemesterRepository;
import com.university.courseenrollment.demogradle.service.auth.LecturerService;
import com.university.courseenrollment.demogradle.service.course.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final LecturerService lecturerService;

    @GetMapping("/list")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String listCourses(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) Long departmentId) {
        var courses = courseService.getAllCourses();
        
        // Apply search filter
        if (search != null && !search.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getCourseCode().toLowerCase().contains(search.toLowerCase()) ||
                                c.getCourseName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        
        // Apply status filter
        if (status != null && !status.isEmpty()) {
            courses = courses.stream()
                    .filter(c -> c.getStatus().toString().equals(status))
                    .toList();
        }
        
        // Apply department filter
        if (departmentId != null) {
            courses = courses.stream()
                    .filter(c -> c.getDepartment() != null && c.getDepartment().getId().equals(departmentId))
                    .toList();
        }
        
        // Avoid LazyInitializationException in templates (open-in-view=false)
        courses.forEach(c -> {
            if (c.getDepartment() != null) {
                c.getDepartment().getDepartmentName();
            }
            if (c.getLecturer() != null) {
                c.getLecturer().getFullName();
            }
        });

        model.addAttribute("courses", courses);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("departmentId", departmentId);

        // course-list.html references these for pagination UI; provide safe defaults
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentPage", 0);
        
        return "course/course-list";
    }

    @GetMapping("/{id}")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String viewCourse(@PathVariable Long id, Model model) {
        var course = courseService.getCourseById(id).orElseThrow();

        // Initialize associations used by the template
        if (course.getDepartment() != null) {
            course.getDepartment().getDepartmentName();
        }
        if (course.getLecturer() != null) {
            course.getLecturer().getFullName();
        }
        if (course.getSemester() != null) {
            course.getSemester().getFullName();
        }
        if (course.getSchedules() != null) {
            course.getSchedules().forEach(s -> {
                if (s.getClassroom() != null) {
                    s.getClassroom().getFullRoomName();
                }
            });
            course.getSchedules().size();
        }
        if (course.getEnrollments() != null) {
            course.getEnrollments().forEach(e -> {
                if (e.getStudent() != null) {
                    e.getStudent().getFullName();
                }
            });
            course.getEnrollments().size();
        }

        model.addAttribute("course", course);
        return "course/course-detail";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String createCoursePage(Model model) {
        model.addAttribute("course", new CourseDTO());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("semesters", semesterRepository.findAll());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        return "course/course-create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String createCourse(@Valid @ModelAttribute("course") CourseDTO courseDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Repopulate dropdowns (same request)
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("semesters", semesterRepository.findAll());
            model.addAttribute("lecturers", lecturerService.getAllLecturers());
            return "course/course-create";
        }

        try {
            var course = courseService.createCourse(courseDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
            return "redirect:/course/" + course.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/course/create";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String editCoursePage(@PathVariable Long id, Model model) {
        var course = courseService.getCourseById(id).orElseThrow();
        model.addAttribute("course", courseService.convertToDTO(course));
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("semesters", semesterRepository.findAll());
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        return "course/course-edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute("course") CourseDTO courseDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Repopulate dropdowns when validation fails
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("semesters", semesterRepository.findAll());
            model.addAttribute("lecturers", lecturerService.getAllLecturers());
            return "course/course-edit";
        }

        try {
            courseService.updateCourse(id, courseDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
            return "redirect:/course/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/course/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/course/list";
    }
}