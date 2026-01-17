package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.model.entity.Admin;
import com.university.courseenrollment.demogradle.service.auth.AdminService;
import com.university.courseenrollment.demogradle.service.auth.LecturerService;
import com.university.courseenrollment.demogradle.service.auth.StudentService;
import com.university.courseenrollment.demogradle.service.auth.UserService;
import com.university.courseenrollment.demogradle.service.course.CourseService;
import com.university.courseenrollment.demogradle.service.course.EnrollmentService;
import com.university.courseenrollment.demogradle.service.FileStorageService;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final StudentService studentService;
    private final LecturerService lecturerService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final FileStorageService fileStorageService;
    private final com.university.courseenrollment.demogradle.repository.DepartmentRepository departmentRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var students = studentService.getAllStudents();
        var lecturers = lecturerService.getAllLecturers();
        var courses = courseService.getAllCourses();

        model.addAttribute("totalStudents", students.size());
        model.addAttribute("totalLecturers", lecturers.size());
        model.addAttribute("totalCourses", courses.size());
        model.addAttribute("activeEnrollments", enrollmentService.getEnrollmentsByStatus(
                com.university.courseenrollment.demogradle.enums.EnrollmentStatus.APPROVED).size());

        // Use the new method that eagerly fetches student and course data
        var recentEnrollments = enrollmentService.getEnrollmentsByStatusWithDetails(
                com.university.courseenrollment.demogradle.enums.EnrollmentStatus.PENDING);
        model.addAttribute("recentEnrollments", recentEnrollments);

        // Lists for dashboard preview panels (top 15 for scrollable view)
        model.addAttribute("recentStudents", students.stream().limit(15).toList());
        model.addAttribute("recentLecturers", lecturers.stream().limit(15).toList());
        model.addAttribute("recentCourses", courses.stream().limit(15).toList());

        return "dashboard/admin-dashboard";
    }

    @GetMapping("/users")
    public String users(Model model,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String role,
                        @RequestParam(required = false) String active) {
        var users = userService.getAllUsers();
        System.out.println("DEBUG: Total users fetched from service: " + users.size());
        users.forEach(u -> System.out.println("DEBUG: User - " + u.getId() + ": " + u.getUsername() + " (roles: " + u.getRoles().size() + ")"));

        // Filter by role if provided (ROLE_STUDENT/ROLE_LECTURER/ROLE_ADMIN)
        if (role != null && !role.isBlank()) {
            users = users.stream()
                    .filter(u -> u.getRoles() != null && u.getRoles().stream().anyMatch(r -> role.equals(r.getName())))
                    .toList();
        }

        // Filter by active status if provided ("true"/"false")
        if (active != null && !active.isBlank()) {
            boolean activeBool = Boolean.parseBoolean(active);
            users = users.stream()
                    .filter(u -> u.isActive() == activeBool)
                    .toList();
        }

        // Search (applies on top of filters)
        if (search != null && !search.isBlank()) {
            String keyword = search.trim();
            users = users.stream()
                    .filter(user -> user.getUsername().contains(keyword)
                            || user.getEmail().contains(keyword)
                            || user.getFullName().contains(keyword))
                    .toList();
        }

        // Show newest first
        users = users.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .toList();

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("role", role);
        model.addAttribute("active", active);
        
        System.out.println("DEBUG: Final users list size added to model: " + users.size());

        return "admin/user-management";
    }
    
    @GetMapping("/users/test")
    public String usersTest(Model model) {
        var users = userService.getAllUsers();
        System.out.println("TEST PAGE: Total users: " + users.size());
        model.addAttribute("users", users);
        return "admin/user-test";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String userType,
                         @RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String firstName,
                         @RequestParam String lastName,
                         @RequestParam(required = false) String phoneNumber,
                         @RequestParam(required = false) String studentId,
                         @RequestParam(required = false) String major,
                         @RequestParam(required = false) Integer yearLevel,
                         @RequestParam(required = false) String lecturerId,
                         @RequestParam(required = false) String specialization,
                         @RequestParam(required = false) String adminId,
                         @RequestParam(required = false) String adminDepartment,
                         RedirectAttributes redirectAttributes) {
        try {
            // Validate common fields
            validateCommonFields(username, email, firstName, lastName);
            
            switch (userType) {
                case "STUDENT":
                    validateStudentFields(studentId, major, yearLevel);
                    com.university.courseenrollment.demogradle.dto.StudentDTO studentDTO = new com.university.courseenrollment.demogradle.dto.StudentDTO();
                    studentDTO.setStudentId(studentId.trim());
                    studentDTO.setUsername(username.trim().toLowerCase());
                    studentDTO.setEmail(email.trim().toLowerCase());
                    studentDTO.setFirstName(firstName.trim());
                    studentDTO.setLastName(lastName.trim());
                    studentDTO.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
                    studentDTO.setMajor(major.trim());
                    studentDTO.setYearLevel(yearLevel);
                    studentService.createStudent(studentDTO);
                    redirectAttributes.addFlashAttribute("successMessage", "Student created successfully with default password: student123");
                    break;

                case "LECTURER":
                    validateLecturerFields(lecturerId);
                    com.university.courseenrollment.demogradle.dto.LecturerDTO lecturerDTO = new com.university.courseenrollment.demogradle.dto.LecturerDTO();
                    lecturerDTO.setEmployeeId(lecturerId.trim());
                    lecturerDTO.setUsername(username.trim().toLowerCase());
                    lecturerDTO.setEmail(email.trim().toLowerCase());
                    lecturerDTO.setFirstName(firstName.trim());
                    lecturerDTO.setLastName(lastName.trim());
                    lecturerDTO.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
                    lecturerDTO.setSpecialization(specialization != null ? specialization.trim() : null);
                    lecturerService.createLecturer(lecturerDTO);
                    redirectAttributes.addFlashAttribute("successMessage", "Lecturer created successfully with default password: lecturer123");
                    return "redirect:/admin/lecturers";  // Redirect to lecturers page instead of users

                case "ADMIN":
                    validateAdminFields(adminId);
                    com.university.courseenrollment.demogradle.dto.AdminDTO adminDTO = new com.university.courseenrollment.demogradle.dto.AdminDTO();
                    adminDTO.setAdminId(adminId.trim());
                    adminDTO.setUsername(username.trim().toLowerCase());
                    adminDTO.setEmail(email.trim().toLowerCase());
                    adminDTO.setFirstName(firstName.trim());
                    adminDTO.setLastName(lastName.trim());
                    adminDTO.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
                    adminDTO.setDepartment(adminDepartment != null ? adminDepartment.trim() : null);
                    adminService.createAdmin(adminDTO);
                    redirectAttributes.addFlashAttribute("successMessage", "Admin created successfully with default password: admin123");
                    break;

                default:
                    throw new IllegalArgumentException("Invalid user type: " + userType);
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    private void validateCommonFields(String username, String email, String firstName, String lastName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!username.matches("^[a-z0-9._]+$")) {
            throw new IllegalArgumentException("Username can only contain lowercase letters, numbers, dots, and underscores");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (firstName.length() > 100) {
            throw new IllegalArgumentException("First name cannot exceed 100 characters");
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (lastName.length() > 100) {
            throw new IllegalArgumentException("Last name cannot exceed 100 characters");
        }
        
        // Check for duplicate username and email
        if (userService.existsByUsername(username.trim().toLowerCase())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userService.existsByEmail(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
    
    private void validateStudentFields(String studentId, String major, Integer yearLevel) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required");
        }
        if (studentId.length() > 20) {
            throw new IllegalArgumentException("Student ID cannot exceed 20 characters");
        }
        
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("Major is required");
        }
        if (major.length() > 100) {
            throw new IllegalArgumentException("Major cannot exceed 100 characters");
        }
        
        if (yearLevel == null || yearLevel < 1 || yearLevel > 4) {
            throw new IllegalArgumentException("Year level must be between 1 and 4");
        }
    }
    
    private void validateLecturerFields(String lecturerId) {
        if (lecturerId == null || lecturerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (lecturerId.length() > 20) {
            throw new IllegalArgumentException("Employee ID cannot exceed 20 characters");
        }
    }
    
    private void validateAdminFields(String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Admin ID is required");
        }
        if (adminId.length() > 20) {
            throw new IllegalArgumentException("Admin ID cannot exceed 20 characters");
        }
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/student-list";
    }

    @PostMapping("/students/{id}/edit")
    public String editStudent(@PathVariable Long id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phoneNumber,
                              @RequestParam(required = false) String major,
                              @RequestParam(required = false) Integer yearLevel,
                              RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Editing student with ID: " + id);
            System.out.println("DEBUG: firstName=" + firstName + ", lastName=" + lastName + ", email=" + email);
            System.out.println("DEBUG: phoneNumber=" + phoneNumber + ", major=" + major + ", yearLevel=" + yearLevel);
            
            com.university.courseenrollment.demogradle.dto.StudentDTO dto = new com.university.courseenrollment.demogradle.dto.StudentDTO();
            dto.setFirstName(firstName.trim());
            dto.setLastName(lastName.trim());
            dto.setEmail(email.trim().toLowerCase());
            dto.setPhoneNumber(phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null);
            dto.setMajor(major != null && !major.trim().isEmpty() ? major.trim() : null);
            dto.setYearLevel(yearLevel);
            
            studentService.updateStudent(id, dto);
            System.out.println("DEBUG: Student updated successfully in database");
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to update student: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/toggle-status")
    public String toggleStudentStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var student = studentService.getStudentById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            String status = student.isActive() ? "deactivated" : "activated";
            userService.toggleUserStatus(student.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Student " + status + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error toggling student status: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Attempting to delete student with ID: " + id);
            
            var student = studentService.getStudentById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            
            System.out.println("DEBUG: Found student: " + student.getFullName());
            
            // Check if student has any enrollments
            var enrollments = enrollmentService.getEnrollmentsByStudent(id);
            if (!enrollments.isEmpty()) {
                System.out.println("DEBUG: Cannot delete - student has " + enrollments.size() + " enrollments");
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot delete student. They have " + enrollments.size() + " enrollment(s). Please remove enrollments first.");
                return "redirect:/admin/students";
            }
            
            // Delete the student (this will cascade to user)
            studentService.deleteStudent(id);
            System.out.println("DEBUG: Student deleted successfully from database");
            
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to delete student: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/lecturers")
    public String lecturers(Model model) {
        model.addAttribute("lecturers", lecturerService.getAllLecturers());
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/lecturer-list";
    }

    @PostMapping("/lecturers/{id}/edit")
    public String editLecturer(@PathVariable Long id,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String officeRoom,
                               @RequestParam(required = false) String specialization,
                               @RequestParam(required = false) Long departmentId,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Editing lecturer with ID: " + id);
            System.out.println("DEBUG: firstName=" + firstName + ", lastName=" + lastName + ", email=" + email);
            System.out.println("DEBUG: phoneNumber=" + phoneNumber + ", officeRoom=" + officeRoom);
            System.out.println("DEBUG: specialization=" + specialization + ", departmentId=" + departmentId);
            
            com.university.courseenrollment.demogradle.dto.LecturerDTO dto = new com.university.courseenrollment.demogradle.dto.LecturerDTO();
            dto.setFirstName(firstName.trim());
            dto.setLastName(lastName.trim());
            dto.setEmail(email.trim().toLowerCase());
            dto.setPhoneNumber(phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null);
            dto.setOfficeRoom(officeRoom != null && !officeRoom.trim().isEmpty() ? officeRoom.trim() : null);
            dto.setSpecialization(specialization != null && !specialization.trim().isEmpty() ? specialization.trim() : null);
            dto.setDepartmentId(departmentId);
            
            lecturerService.updateLecturer(id, dto);
            System.out.println("DEBUG: Lecturer updated successfully in database");
            redirectAttributes.addFlashAttribute("successMessage", "Lecturer updated successfully");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to update lecturer: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating lecturer: " + e.getMessage());
        }
        return "redirect:/admin/lecturers";
    }

    @PostMapping("/lecturers/{id}/toggle-status")
    public String toggleLecturerStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var lecturer = lecturerService.getLecturerById(id)
                    .orElseThrow(() -> new RuntimeException("Lecturer not found"));
            
            // Lecturer extends User, so we can get the ID directly
            String status = lecturer.isActive() ? "deactivated" : "activated";
            userService.toggleUserStatus(lecturer.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Lecturer " + status + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error toggling lecturer status: " + e.getMessage());
        }
        return "redirect:/admin/lecturers";
    }

    @PostMapping("/lecturers/{id}/delete")
    public String deleteLecturer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("DEBUG: Attempting to delete lecturer with ID: " + id);
            
            var lecturer = lecturerService.getLecturerById(id)
                    .orElseThrow(() -> new RuntimeException("Lecturer not found"));
            
            System.out.println("DEBUG: Found lecturer: " + lecturer.getFullName());
            
            // Check if lecturer has any courses assigned
            var courses = courseService.getCoursesByLecturer(id);
            if (!courses.isEmpty()) {
                System.out.println("DEBUG: Cannot delete - lecturer has " + courses.size() + " courses");
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot delete lecturer. They have " + courses.size() + " course(s) assigned. Please reassign courses first.");
                return "redirect:/admin/lecturers";
            }
            
            // Delete the lecturer (this will cascade to user)
            lecturerService.deleteLecturer(id);
            System.out.println("DEBUG: Lecturer deleted successfully from database");
            
            redirectAttributes.addFlashAttribute("successMessage", "Lecturer deleted successfully");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to delete lecturer: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting lecturer: " + e.getMessage());
        }
        return "redirect:/admin/lecturers";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("totalStudents", studentService.getAllStudents().size());
        model.addAttribute("totalLecturers", lecturerService.getAllLecturers().size());
        model.addAttribute("totalCourses", courseService.getAllCourses().size());
        return "admin/reports";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "admin/system-settings";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Admin admin = adminService.getAdminByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            
            model.addAttribute("admin", admin);
            return "admin/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Admin admin = adminService.getAdminByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
            
            model.addAttribute("admin", admin);
            return "admin/edit-profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/edit-profile")
    @org.springframework.transaction.annotation.Transactional
    public String updateProfile(@RequestParam(required = false) String phoneNumber,
                                @RequestParam(required = false) String department,
                                @RequestParam(required = false) MultipartFile profilePicture,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Admin admin = adminService.getAdminByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Handle profile picture upload
            String profilePicturePath = admin.getProfilePicture();
            if (profilePicture != null && !profilePicture.isEmpty()) {
                // Validate file type
                String contentType = profilePicture.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Please upload a valid image file (JPG, PNG, GIF)");
                    return "redirect:/admin/edit-profile";
                }
                
                // Validate file size (max 5MB)
                if (profilePicture.getSize() > 5 * 1024 * 1024) {
                    redirectAttributes.addFlashAttribute("errorMessage", "File size must be less than 5MB");
                    return "redirect:/admin/edit-profile";
                }
                
                // Delete old profile picture if exists
                if (admin.getProfilePicture() != null && !admin.getProfilePicture().isEmpty()) {
                    fileStorageService.deleteFile(admin.getProfilePicture());
                }
                
                // Store new profile picture
                profilePicturePath = fileStorageService.storeFile(profilePicture);
            }

            // Update allowed fields using DTO
            com.university.courseenrollment.demogradle.dto.AdminDTO dto = new com.university.courseenrollment.demogradle.dto.AdminDTO();
            dto.setEmail(admin.getEmail());
            dto.setFirstName(admin.getFirstName());
            dto.setLastName(admin.getLastName());
            dto.setPhoneNumber(phoneNumber != null ? phoneNumber : admin.getPhoneNumber());
            dto.setProfilePicture(profilePicturePath);
            dto.setDepartment(department != null ? department : admin.getDepartment());
            dto.setAdminLevel(admin.getAdminLevel()); // Preserve admin level
            
            adminService.updateAdmin(admin.getId(), dto);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/admin/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/admin/edit-profile";
        }
    }

    @PostMapping("/delete-profile-picture")
    @org.springframework.transaction.annotation.Transactional
    public String deleteProfilePicture(RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Admin admin = adminService.getAdminByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            // Delete profile picture file
            if (admin.getProfilePicture() != null && !admin.getProfilePicture().isEmpty()) {
                fileStorageService.deleteFile(admin.getProfilePicture());
                
                // Update database
                com.university.courseenrollment.demogradle.dto.AdminDTO dto = new com.university.courseenrollment.demogradle.dto.AdminDTO();
                dto.setEmail(admin.getEmail());
                dto.setFirstName(admin.getFirstName());
                dto.setLastName(admin.getLastName());
                dto.setPhoneNumber(admin.getPhoneNumber());
                dto.setProfilePicture(null);
                dto.setDepartment(admin.getDepartment());
                dto.setAdminLevel(admin.getAdminLevel()); // Preserve admin level
                
                adminService.updateAdmin(admin.getId(), dto);
                
                redirectAttributes.addFlashAttribute("successMessage", "Profile picture deleted successfully");
            }
            
            return "redirect:/admin/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting profile picture: " + e.getMessage());
            return "redirect:/admin/profile";
        }
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var course = courseService.getCourseById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            
            // Check if course has any enrollments
            var enrollments = enrollmentService.getEnrollmentsByCourse(id);
            if (!enrollments.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot delete course. It has " + enrollments.size() + " enrollment(s). Please remove enrollments first.");
                return "redirect:/course/list";
            }
            
            // Check if course has any schedules
            var schedules = course.getSchedules();
            if (schedules != null && !schedules.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Cannot delete course. It has " + schedules.size() + " schedule(s). Please remove schedules first.");
                return "redirect:/course/list";
            }
            
            // Delete the course
            courseService.deleteCourse(id);
            
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/course/list";
    }

    @GetMapping("/enrollments")
    public String viewAllEnrollments(Model model, 
                                     @RequestParam(required = false) String status) {
        var enrollments = status != null && !status.isEmpty() 
            ? enrollmentService.getEnrollmentsByStatus(
                com.university.courseenrollment.demogradle.enums.EnrollmentStatus.valueOf(status))
            : enrollmentService.getAllEnrollments();
        
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("status", status);
        return "admin/enrollment-list";
    }

    @PostMapping("/enrollments/{id}/approve")
    public String approveEnrollment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.approveEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/enrollments";
    }

    @PostMapping("/enrollments/{id}/reject")
    public String rejectEnrollment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.rejectEnrollment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Enrollment rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/enrollments";
    }
}
