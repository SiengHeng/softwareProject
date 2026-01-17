package com.university.courseenrollment.demogradle.controller.web;

import com.university.courseenrollment.demogradle.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Unified profile controller that redirects users to their role-specific profile pages
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    @GetMapping
    public String redirectToRoleProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        // Check user role and redirect accordingly
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/profile";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LECTURER"))) {
            return "redirect:/lecturer/profile";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT"))) {
            return "redirect:/student/profile";
        }

        // Fallback to login if no matching role
        return "redirect:/auth/login";
    }
}
