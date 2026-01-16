package com.university.courseenrollment.controller;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.university.courseenrollment.entity.Role;
import com.university.courseenrollment.entity.User;
import com.university.courseenrollment.repository.RoleRepository;
import com.university.courseenrollment.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public AuthController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        Role studentRole = roleRepository.findByName("STUDENT");
        user.setRoles(Set.of(studentRole));
        userService.save(user);
        return "redirect:/login";
    }
}
