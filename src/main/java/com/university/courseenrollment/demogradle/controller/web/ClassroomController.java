package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.dto.ClassroomDTO;
import com.university.courseenrollment.demogradle.service.schedule.ClassroomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/classroom")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping("/list")
    public String listClassrooms(Model model,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String building,
                                 @RequestParam(required = false) String available) {
        var classrooms = classroomService.getAllClassrooms();

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("search", search);
        model.addAttribute("building", building);
        model.addAttribute("available", available);

        // Statistics
        model.addAttribute("totalClassrooms", classrooms.size());
        model.addAttribute("availableClassrooms", classrooms.stream().filter(c -> c.isAvailable()).count());
        model.addAttribute("totalCapacity", classrooms.stream().mapToInt(c -> c.getCapacity()).sum());
        model.addAttribute("withProjector", classrooms.stream().filter(c -> c.isHasProjector()).count());

        // Get unique buildings
        model.addAttribute("buildings", classrooms.stream()
                .map(c -> c.getBuilding())
                .distinct()
                .toList());

        return "classroom/classroom-list";
    }

    @GetMapping("/create")
    public String createClassroomPage(Model model) {
        model.addAttribute("classroom", new ClassroomDTO());
        return "classroom/classroom-create";
    }

    @PostMapping("/create")
    public String createClassroom(@Valid @ModelAttribute("classroom") ClassroomDTO classroomDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "classroom/classroom-create";
        }

        try {
            classroomService.createClassroom(classroomDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Classroom created successfully");
            return "redirect:/classroom/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/classroom/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editClassroomPage(@PathVariable Long id, Model model) {
        var classroom = classroomService.getClassroomById(id).orElseThrow();
        model.addAttribute("classroom", classroomService.convertToDTO(classroom));
        return "classroom/classroom-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateClassroom(@PathVariable Long id,
                                  @Valid @ModelAttribute("classroom") ClassroomDTO classroomDTO,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "classroom/classroom-edit";
        }

        try {
            classroomService.updateClassroom(id, classroomDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Classroom updated successfully");
            return "redirect:/classroom/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/classroom/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteClassroom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.deleteClassroom(id);
            redirectAttributes.addFlashAttribute("successMessage", "Classroom deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/classroom/list";
    }

    @GetMapping("/availability/{id}")
    public String classroomAvailability(@PathVariable Long id, Model model) {
        var classroom = classroomService.getClassroomById(id).orElseThrow();
        model.addAttribute("classroom", classroom);
        // Get schedules for this classroom if they exist
        if (classroom.getSchedules() != null) {
            model.addAttribute("schedules", classroom.getSchedules());
        }
        return "classroom/classroom-availability";
    }
}
