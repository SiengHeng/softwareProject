package com.example.demo.controller;

import com.example.demo.service.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam Long studentId, Model model) {
        model.addAttribute("enrollments", service.getEnrollments(studentId));
        return "student-dashboard";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam Long studentId,
                         @RequestParam Long courseId) {
        service.enroll(studentId, courseId);
        return "redirect:/student/dashboard?studentId=" + studentId;
    }

    @PostMapping("/drop")
    public String drop(@RequestParam Long studentId,
                       @RequestParam Long courseId) {
        service.drop(studentId, courseId);
        return "redirect:/student/dashboard?studentId=" + studentId;
    }
}
