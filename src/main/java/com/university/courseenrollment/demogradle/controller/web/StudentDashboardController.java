package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.model.entity.Enrollment;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.enums.EnrollmentStatus;
import com.university.courseenrollment.demogradle.service.auth.StudentService;
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
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentDashboardController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final FileStorageService fileStorageService;

    @GetMapping("/dashboard")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String dashboard(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Get enrollments with eagerly loaded course and lecturer
            List<Enrollment> enrollments = student.getId() != null ? 
                    enrollmentService.getEnrollmentsByStudent(student.getId()).stream()
                            .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                            .toList() : 
                    Collections.emptyList();

            int totalCredits = enrollments.stream()
                    .mapToInt(enrollment -> {
                        if (enrollment.getCourse() != null) {
                            return enrollment.getCourse().getCredits();
                        }
                        return 0;
                    })
                    .sum();

            model.addAttribute("student", student);
            model.addAttribute("enrollments", enrollments);
            model.addAttribute("enrolledCount", enrollments.size());
            model.addAttribute("totalCredits", totalCredits);
            model.addAttribute("gpa", student.getGpa() != null ? student.getGpa() : 0.0);
            model.addAttribute("yearLevel", student.getYearLevel() != null ? student.getYearLevel() : 1);

            return "dashboard/student-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            model.addAttribute("errorDetails", e.getClass().getName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                model.addAttribute("cause", e.getCause().getMessage());
            }
            return "error/500";
        }
    }

    @GetMapping("/profile")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String profile(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Eagerly fetch department to avoid LazyInitializationException
            if (student.getDepartment() != null) {
                student.getDepartment().getDepartmentName(); // Force initialization
            }

            // Get enrollments with approved status
            List<Enrollment> enrollments = student.getId() != null ? 
                    enrollmentService.getEnrollmentsByStudent(student.getId()).stream()
                            .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                            .toList() : 
                    Collections.emptyList();

            int totalCredits = enrollments.stream()
                    .mapToInt(enrollment -> {
                        if (enrollment.getCourse() != null) {
                            return enrollment.getCourse().getCredits();
                        }
                        return 0;
                    })
                    .sum();

            model.addAttribute("student", student);
            model.addAttribute("enrolledCourses", enrollments.size());
            model.addAttribute("totalCredits", totalCredits);
            return "student/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/my-schedule")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String mySchedule(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Get enrollments with approved status
            List<Enrollment> enrollments = student.getId() != null ? 
                    enrollmentService.getEnrollmentsByStudent(student.getId()).stream()
                            .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                            .toList() : 
                    Collections.emptyList();

            model.addAttribute("student", student);
            model.addAttribute("enrollments", enrollments);
            return "student/my-schedule";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading schedule: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/courses")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String myCourses(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Get enrollments with approved status
            List<Enrollment> enrollments = student.getId() != null ? 
                    enrollmentService.getEnrollmentsByStudent(student.getId()).stream()
                            .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                            .toList() : 
                    Collections.emptyList();

            model.addAttribute("student", student);
            model.addAttribute("enrollments", enrollments);
            return "course/my-courses";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading courses: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/grades")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String myGrades(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Get all enrollments (approved and completed)
            List<Enrollment> enrollments = student.getId() != null ? 
                    enrollmentService.getEnrollmentsByStudent(student.getId()) : 
                    Collections.emptyList();

            model.addAttribute("student", student);
            model.addAttribute("enrollments", enrollments);
            model.addAttribute("gpa", student.getGpa() != null ? student.getGpa() : 0.0);
            return "student/grades";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading grades: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/edit-profile")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String editProfile(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            model.addAttribute("student", student);
            return "student/edit-profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/edit-profile")
    @org.springframework.transaction.annotation.Transactional
    public String updateProfile(@RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) MultipartFile profilePicture,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            String profilePicturePath = student.getProfilePicture();
            // Handle profile picture upload
            if (profilePicture != null && !profilePicture.isEmpty()) {
                String contentType = profilePicture.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please upload a valid image file");
                    return "redirect:/student/edit-profile";
                }
                if (profilePicture.getSize() > 5 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("errorMessage", "File size must be less than 5MB");
                    return "redirect:/student/edit-profile";
                }

                // Delete old profile picture if exists
                if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()) {
                    fileStorageService.deleteFile(student.getProfilePicture());
                }

                // Store new profile picture
                profilePicturePath = fileStorageService.storeFile(profilePicture);
            }

            // Update allowed fields using DTO
            com.university.courseenrollment.demogradle.dto.StudentDTO dto = new com.university.courseenrollment.demogradle.dto.StudentDTO();
            dto.setEmail(email != null && !email.isEmpty() ? email : student.getEmail());
            dto.setFirstName(firstName != null && !firstName.isEmpty() ? firstName : student.getFirstName());
            dto.setLastName(lastName != null && !lastName.isEmpty() ? lastName : student.getLastName());
            dto.setPhoneNumber(phoneNumber != null ? phoneNumber : student.getPhoneNumber());
            dto.setMajor(student.getMajor());
            dto.setYearLevel(student.getYearLevel());
            dto.setProfilePicture(profilePicturePath);

            studentService.updateStudent(student.getId(), dto);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/student/edit-profile";
        }
    }

    @PostMapping("/delete-profile-picture")
    @org.springframework.transaction.annotation.Transactional
    public String deleteProfilePicture(RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Student student = studentService.getStudentByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Delete profile picture file
            if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()) {
                fileStorageService.deleteFile(student.getProfilePicture());
                student.setProfilePicture(null);
                
                // Update student
                com.university.courseenrollment.demogradle.dto.StudentDTO dto = new com.university.courseenrollment.demogradle.dto.StudentDTO();
                dto.setEmail(student.getEmail());
                dto.setFirstName(student.getFirstName());
                dto.setLastName(student.getLastName());
                dto.setPhoneNumber(student.getPhoneNumber());
                dto.setMajor(student.getMajor());
                dto.setYearLevel(student.getYearLevel());
                dto.setProfilePicture(null);

                studentService.updateStudent(student.getId(), dto);
                
                redirectAttributes.addFlashAttribute("successMessage", "Profile picture deleted successfully");
            }
            
            return "redirect:/student/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting profile picture: " + e.getMessage());
            return "redirect:/student/profile";
        }
    }
}