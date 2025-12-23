package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ADMIN") == null) {
            roleRepository.save(new Role("ADMIN"));
        }
        if (roleRepository.findByName("LECTURER") == null) {
            roleRepository.save(new Role("LECTURER"));
        }
        if (roleRepository.findByName("STUDENT") == null) {
            roleRepository.save(new Role("STUDENT"));
        }

        if (userRepository.findByUsername("admin") == null) {
            Role adminRole = roleRepository.findByName("ADMIN");
            User adminUser = new User("admin", passwordEncoder.encode("password"), Set.of(adminRole));
            userRepository.save(adminUser);
        }
    }
}
