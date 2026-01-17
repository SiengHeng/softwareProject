package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        if (SecurityUtils.hasRole("ROLE_STUDENT")) {
            return "redirect:/student/dashboard";
        } else if (SecurityUtils.hasRole("ROLE_LECTURER")) {
            return "redirect:/lecturer/dashboard";
        } else if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/auth/login";
    }
}