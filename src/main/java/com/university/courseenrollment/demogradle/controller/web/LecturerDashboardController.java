package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.model.entity.Lecturer;
import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import com.university.courseenrollment.demogradle.service.auth.LecturerService;
import com.university.courseenrollment.demogradle.service.course.CourseService;
import com.university.courseenrollment.demogradle.service.course.EnrollmentService;
import com.university.courseenrollment.demogradle.service.FileStorageService;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lecturer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LECTURER')")
public class LecturerDashboardController {

    private final LecturerService lecturerService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final FileStorageService fileStorageService;
    private final com.university.courseenrollment.demogradle.service.schedule.ScheduleService scheduleService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();

        var courses = courseService.getCoursesByLecturer(lecturer.getId());

        int totalStudents = courses.stream()
                .mapToInt(c -> c.getCurrentEnrolled())
                .sum();

        long pendingEnrollments = enrollmentService.getEnrollmentsByStatus(EnrollmentStatus.PENDING).stream()
                .filter(e -> courses.stream().anyMatch(c -> c.getId().equals(e.getCourse().getId())))
                .count();

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("courses", courses);
        model.addAttribute("courseCount", courses.size());
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("pendingEnrollments", pendingEnrollments);

        return "dashboard/lecturer-dashboard";
    }

    @GetMapping("/profile")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String profile(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();

        // Avoid LazyInitializationException when templates access department
        if (lecturer.getDepartment() != null) {
            lecturer.getDepartment().getDepartmentName();
        }

        model.addAttribute("lecturer", lecturer);
        return "lecturer/profile";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();
        var courses = courseService.getCoursesByLecturer(lecturer.getId());
        model.addAttribute("courses", courses);
        model.addAttribute("lecturer", lecturer);
        return "lecturer/my-courses";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();
        model.addAttribute("lecturer", lecturer);
        return "lecturer/edit-profile";
    }

    @PostMapping("/edit-profile")
    @org.springframework.transaction.annotation.Transactional
    public String updateProfile(@RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) String officeRoom,
                                @RequestParam(required = false) String specialization,
                                @RequestParam(required = false) MultipartFile profilePicture,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername().orElseThrow();
            Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();

            String profilePicturePath = lecturer.getProfilePicture();
            if (profilePicture != null && !profilePicture.isEmpty()) {
                String contentType = profilePicture.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please upload a valid image file");
                    return "redirect:/lecturer/edit-profile";
                }
                if (profilePicture.getSize() > 5 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("errorMessage", "File size must be less than 5MB");
                    return "redirect:/lecturer/edit-profile";
                }

                if (lecturer.getProfilePicture() != null && !lecturer.getProfilePicture().isEmpty()) {
                    fileStorageService.deleteFile(lecturer.getProfilePicture());
                }
                profilePicturePath = fileStorageService.storeFile(profilePicture);
            }

            // Update allowed fields using DTO
            com.university.courseenrollment.demogradle.dto.LecturerDTO dto = new com.university.courseenrollment.demogradle.dto.LecturerDTO();
            dto.setEmail(email != null && !email.isEmpty() ? email : lecturer.getEmail());
            dto.setFirstName(firstName != null && !firstName.isEmpty() ? firstName : lecturer.getFirstName());
            dto.setLastName(lastName != null && !lastName.isEmpty() ? lastName : lecturer.getLastName());
            dto.setPhoneNumber(phoneNumber != null ? phoneNumber : lecturer.getPhoneNumber());
            dto.setOfficeRoom(officeRoom != null ? officeRoom : lecturer.getOfficeRoom());
            dto.setSpecialization(specialization != null ? specialization : lecturer.getSpecialization());
            dto.setProfilePicture(profilePicturePath);

            lecturerService.updateLecturer(lecturer.getId(), dto);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/lecturer/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/lecturer/edit-profile";
        }
    }

    @GetMapping("/attendance")
    public String attendance(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();
        var courses = courseService.getCoursesByLecturer(lecturer.getId());
        
        model.addAttribute("courses", courses);
        model.addAttribute("lecturer", lecturer);
        return "lecturer/attandance"; // Note: typo in template name matches existing file
    }

    @GetMapping("/my-schedule")
    public String mySchedule(Model model) {
        String username = SecurityUtils.getCurrentUsername().orElseThrow();
        Lecturer lecturer = lecturerService.getLecturerByUsername(username).orElseThrow();
        var courses = courseService.getCoursesByLecturer(lecturer.getId());
        
        // Get all schedules for lecturer's courses
        var schedules = courses.stream()
                .flatMap(course -> scheduleService.getSchedulesByCourse(course.getId()).stream())
                .toList();
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("courses", courses);
        model.addAttribute("lecturer", lecturer);
        return "lecturer/my-schedule";
    }
}
