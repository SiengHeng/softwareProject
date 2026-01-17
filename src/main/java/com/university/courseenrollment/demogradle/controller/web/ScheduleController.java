package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.dto.ScheduleDTO;
import com.university.courseenrollment.demogradle.repository.ClassroomRepository;
import com.university.courseenrollment.demogradle.repository.CourseRepository;
import com.university.courseenrollment.demogradle.service.schedule.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final CourseRepository courseRepository;
    private final ClassroomRepository classroomRepository;

    @GetMapping("/list")
    public String listSchedules(Model model,
                                @RequestParam(required = false) String dayOfWeek,
                                @RequestParam(required = false) Long classroomId,
                                @RequestParam(required = false) String search) {
        var schedules = scheduleService.getAllSchedules();

        // Generate time slots for the weekly view
        java.util.List<String> timeSlots = java.util.Arrays.asList(
            "08:00", "09:00", "10:00", "11:00", "12:00", 
            "13:00", "14:00", "15:00", "16:00", "17:00"
        );

        model.addAttribute("schedules", schedules);
        model.addAttribute("classrooms", classroomRepository.findAll());
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("dayOfWeek", dayOfWeek);
        model.addAttribute("classroomId", classroomId);
        model.addAttribute("search", search);

        return "schedule/schedule-list";
    }

    @GetMapping("/create")
    public String createSchedulePage(Model model) {
        model.addAttribute("schedule", new ScheduleDTO());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("classrooms", classroomRepository.findAll());
        return "schedule/schedule-create";
    }

    @PostMapping("/create")
    public String createSchedule(@Valid @ModelAttribute("schedule") ScheduleDTO scheduleDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseRepository.findAll());
            model.addAttribute("classrooms", classroomRepository.findAll());
            return "schedule/schedule-create";
        }

        try {
            scheduleService.createSchedule(scheduleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule created successfully");
            return "redirect:/schedule/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/schedule/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editSchedulePage(@PathVariable Long id, Model model) {
        var schedule = scheduleService.getScheduleById(id).orElseThrow();
        model.addAttribute("schedule", scheduleService.convertToDTO(schedule));
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("classrooms", classroomRepository.findAll());
        return "schedule/schedule-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateSchedule(@PathVariable Long id,
                                 @Valid @ModelAttribute("schedule") ScheduleDTO scheduleDTO,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseRepository.findAll());
            model.addAttribute("classrooms", classroomRepository.findAll());
            return "schedule/schedule-edit";
        }

        try {
            scheduleService.updateSchedule(id, scheduleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully");
            return "redirect:/schedule/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/schedule/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteSchedule(id);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/schedule/list";
    }

    @GetMapping("/calendar")
    public String calendar(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "schedule/schedule-calendar";
    }

    @GetMapping("/timetable")
    public String timetable(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "schedule/timetable";
    }
}
