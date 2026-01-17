package com.university.courseenrollment.demogradle.controller.api;

import com.university.courseenrollment.demogradle.dto.LoginRequest;
import com.university.courseenrollment.demogradle.service.auth.AuthenticationService;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            var user = authenticationService.login(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authenticationService.logout();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = SecurityUtils.getCurrentUsername()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            response.put("authenticated", "true");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }
    }
}