package com.university.courseenrollment.demogradle.config;

import com.university.courseenrollment.demogradle.repository.UserRepository;
import com.university.courseenrollment.demogradle.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PasswordInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeUniquePasswords() {
        return args -> {
            // Check if passwords need updating (if they're all the same, we need to update)
            Map<String, String> uniquePasswords = new HashMap<>();
            
            // Admin
            uniquePasswords.put("admin", "Admin@2024");
            
            // Lecturers - each gets unique password
            uniquePasswords.put("john.smith", "Lecturer@john");
            uniquePasswords.put("sarah.johnson", "Lecturer@sarah");
            uniquePasswords.put("michael.brown", "Lecturer@michael");
            uniquePasswords.put("emily.davis", "Lecturer@emily");
            
            // Students - each gets unique password
            uniquePasswords.put("alice.wilson", "Student@alice");
            uniquePasswords.put("bob.martinez", "Student@bob");
            uniquePasswords.put("carol.garcia", "Student@carol");
            uniquePasswords.put("david.lee", "Student@david");
            uniquePasswords.put("emma.taylor", "Student@emma");
            
            // Update each user's password
            uniquePasswords.forEach((username, rawPassword) -> {
                userRepository.findByUsername(username).ifPresent(user -> {
                    String encodedPassword = passwordEncoder.encode(rawPassword);
                    // Only update if the current password doesn't match the new one
                    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        user.setPassword(encodedPassword);
                        userRepository.save(user);
                        log.info("Updated unique password for user: {}", username);
                    }
                });
            });
            
            log.info("Password initialization completed. All users now have unique passwords.");
        };
    }
}
