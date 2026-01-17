package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.service.auth.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public String listStudents(Model model, @RequestParam(required = false) String search) {
        var students = studentService.getAllStudents();
        
        if (search != null && !search.isEmpty()) {
            // In a real app, you'd filter here or use a service method
            // For now, returning all
        }
        
        model.addAttribute("students", students);
        model.addAttribute("search", search);
        return "student/student-list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        var student = studentService.getStudentById(id).orElseThrow();
        model.addAttribute("student", student);
        return "student/student-detail";
    }
}
