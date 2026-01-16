package com.university.courseenrollment.demogradle.security;

import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.repository.UserRepository;
import com.university.courseenrollment.demogradle.service.audit.AuditService;
import com.university.courseenrollment.demogradle.service.session.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuditService auditService;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws ServletException, IOException {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // Record successful login
                auditService.recordLogin(user, request, "SUCCESS");
                
                // Create session
                sessionService.createSession(user, request);
                
                // Log activity
                auditService.logActivity(user, "LOGIN", "User", user.getId(), null, null, 
                    "User logged in successfully");
                
                log.info("User logged in successfully: {}", username);
            }
        } catch (Exception e) {
            log.error("Error in authentication success handler", e);
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
