package com.university.courseenrollment.demogradle.security;

import com.university.courseenrollment.demogradle.model.entity.Role;
import com.university.courseenrollment.demogradle.model.entity.Student;
import com.university.courseenrollment.demogradle.repository.RoleRepository;
import com.university.courseenrollment.demogradle.repository.StudentRepository;
import com.university.courseenrollment.demogradle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Check if user already exists
        Student existingStudent = studentRepository.findByEmail(email).orElse(null);
        
        if (existingStudent == null) {
            // Auto-register new student
            existingStudent = registerNewStudent(email, name, registrationId, oAuth2User);
            log.info("Auto-registered new student via {}: {}", registrationId, email);
        } else {
            log.info("Existing student logged in via {}: {}", registrationId, email);
        }

        return oAuth2User;
    }

    private Student registerNewStudent(String email, String name, String provider, OAuth2User oAuth2User) {
        Student student = new Student();
        
        // Generate username from email
        String username = email.split("@")[0];
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = email.split("@")[0] + counter++;
        }
        
        student.setUsername(username);
        student.setEmail(email);
        
        // Parse name
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ");
            student.setFirstName(nameParts[0]);
            student.setLastName(nameParts.length > 1 ? nameParts[nameParts.length - 1] : "");
        } else {
            student.setFirstName(username);
            student.setLastName("");
        }
        
        // Generate random password (they'll use OAuth2 to login)
        // Use BCrypt directly to avoid circular dependency
        student.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
        
        // Generate student ID
        String studentId = generateStudentId();
        student.setStudentId(studentId);
        
        // Set default values
        student.setMajor("Undeclared"); // Can be updated later
        student.setPhoneNumber("");
        student.setActive(true);
        
        // Get additional info from OAuth2 provider
        if ("google".equals(provider)) {
            String picture = oAuth2User.getAttribute("picture");
            // Store picture URL if needed
        } else if ("github".equals(provider)) {
            String login = oAuth2User.getAttribute("login");
            String avatarUrl = oAuth2User.getAttribute("avatar_url");
            // Store additional GitHub info if needed
        }
        
        // Assign STUDENT role
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RuntimeException("Student role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);
        
        Student savedStudent = studentRepository.save(student);
        
        // Log will be done by OAuth2LoginSuccessHandler to avoid circular dependency
        log.info("Student auto-registered via {} OAuth2: {} ({})", provider, savedStudent.getUsername(), savedStudent.getEmail());
        
        return savedStudent;
    }

    private String generateStudentId() {
        // Generate unique student ID: STU + year + random 4 digits
        String year = String.valueOf(java.time.Year.now().getValue()).substring(2);
        int random = (int) (Math.random() * 9000) + 1000;
        String studentId = "STU" + year + random;
        
        // Ensure uniqueness
        while (studentRepository.existsByStudentId(studentId)) {
            random = (int) (Math.random() * 9000) + 1000;
            studentId = "STU" + year + random;
        }
        
        return studentId;
    }
}
