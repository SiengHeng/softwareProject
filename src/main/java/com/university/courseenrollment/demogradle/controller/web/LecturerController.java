package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.service.auth.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lecturers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LecturerController {

    private final LecturerService lecturerService;

    @GetMapping
    public String listLecturers(Model model, @RequestParam(required = false) String search) {
        var lecturers = lecturerService.getAllLecturers();
        
        if (search != null && !search.isEmpty()) {
            // In a real app, you'd filter here or use a service method
        }
        
        model.addAttribute("lecturers", lecturers);
        model.addAttribute("search", search);
        return "lecturer/lecturer-list";
    }

    @GetMapping("/{id}")
    public String viewLecturer(@PathVariable Long id, Model model) {
        var lecturer = lecturerService.getLecturerById(id).orElseThrow();
        model.addAttribute("lecturer", lecturer);
        return "lecturer/lecturer-detail";
    }
}
