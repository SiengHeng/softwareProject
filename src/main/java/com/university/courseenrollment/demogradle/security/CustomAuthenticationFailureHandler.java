package com.university.courseenrollment.demogradle.security;

import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.repository.UserRepository;
import com.university.courseenrollment.demogradle.service.audit.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuditService auditService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        try {
            String username = request.getParameter("username");
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // Record failed login
                auditService.recordLogin(user, request, "FAILED");
                log.warn("Failed login attempt for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Error in authentication failure handler", e);
        }
        
        setDefaultFailureUrl("/auth/login?error=true");
        super.onAuthenticationFailure(request, response, exception);
    }
}
