package com.university.courseenrollment.demogradle.security;

import  com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.repository.UserRepository;
import com.university.courseenrollment.demogradle.service.audit.AuditService;
import com.university.courseenrollment.demogradle.service.session.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuditService auditService;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            
            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                
                if (user != null) {
                    // Record successful OAuth2 login
                    auditService.recordLogin(user, request, "SUCCESS");
                    
                    // Create session
                    sessionService.createSession(user, request);
                    
                    // Log activity
                    auditService.logActivity(user, "LOGIN", "User", user.getId(), null, null, 
                        "User logged in via OAuth2");
                    
                    log.info("OAuth2 user logged in successfully: {}", user.getUsername());
                }
            }
            
            setDefaultTargetUrl("/dashboard");
            super.onAuthenticationSuccess(request, response, authentication);
            
        } catch (Exception e) {
            log.error("Error in OAuth2 authentication success handler", e);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
