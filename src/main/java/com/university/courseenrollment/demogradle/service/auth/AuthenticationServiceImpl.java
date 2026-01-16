package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.dto.LoginRequest;
import com.university.courseenrollment.demogradle.dto.RegistrationRequest;
import com.university.courseenrollment.demogradle.model.entity.Role;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.repository.RoleRepository;
import com.university.courseenrollment.demogradle.repository.StudentRepository;
import com.university.courseenrollment.demogradle.repository.UserRepository;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import com.university.courseenrollment.demogradle.service.audit.AuditService;
import com.university.courseenrollment.demogradle.service.session.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final SessionService sessionService;

    @Override
    @Transactional
    public User login(LoginRequest request) {
        HttpServletRequest httpRequest = getHttpRequest();
        User user = null;
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Record successful login
            if (httpRequest != null) {
                auditService.recordLogin(user, httpRequest, "SUCCESS");
                sessionService.createSession(user, httpRequest);
            }
            
            // Log activity
            auditService.logActivity(user, "LOGIN", "User", user.getId(), null, null, 
                "User logged in successfully");
            
            log.info("User logged in successfully: {}", user.getUsername());
            return user;
            
        } catch (AuthenticationException e) {
            // Record failed login attempt
            user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user != null && httpRequest != null) {
                auditService.recordLogin(user, httpRequest, "FAILED");
            }
            log.warn("Failed login attempt for username: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public User register(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (studentRepository.existsByStudentId(request.getStudentId())) {
            throw new RuntimeException("Student ID already exists");
        }

        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setEmail(request.getEmail());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setStudentId(request.getStudentId());
        student.setMajor(request.getMajor());
        student.setActive(true);

        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RuntimeException("Student role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);

        Student savedStudent = studentRepository.save(student);
        
        // Log registration activity
        auditService.logActivity("CREATE", "Student", savedStudent.getId(), 
            "New student registered: " + savedStudent.getUsername());
        
        log.info("New student registered: {}", savedStudent.getUsername());
        return savedStudent;
    }
    
    private HttpServletRequest getHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            log.warn("Unable to get HTTP request", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void logout() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                // Invalidate all active sessions
                sessionService.invalidateAllUserSessions(currentUser.getId());
                
                // Log logout activity
                auditService.logActivity(currentUser, "LOGOUT", "User", currentUser.getId(), 
                    null, null, "User logged out");
                
                log.info("User logged out: {}", currentUser.getUsername());
            }
        } catch (Exception e) {
            log.error("Error during logout", e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public User getCurrentUser() {
        return SecurityUtils.getCurrentUsername()
                .flatMap(userRepository::findByUsername)
                .orElse(null);
    }

    @Override
    public boolean isAuthenticated() {
        return SecurityUtils.isAuthenticated();
    }
}
