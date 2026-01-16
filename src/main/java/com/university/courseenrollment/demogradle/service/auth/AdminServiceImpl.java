package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.exception.DuplicateResourceException;
import com.university.courseenrollment.demogradle.exception.ResourceNotFoundException;
import com.university.courseenrollment.demogradle.dto.AdminDTO;
import com.university.courseenrollment.demogradle.model.entity.Admin;
import com.university.courseenrollment.demogradle.model.entity.Role;
import com.university.courseenrollment.demogradle.repository.AdminRepository;
import com.university.courseenrollment.demogradle.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Admin createAdmin(AdminDTO dto) {
        if (adminRepository.existsByAdminId(dto.getAdminId())) {
            throw new DuplicateResourceException("Admin ID already exists: " + dto.getAdminId());
        }

        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode("admin123")); // Default password
        admin.setEmail(dto.getEmail());
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setPhoneNumber(dto.getPhoneNumber());
        admin.setAdminId(dto.getAdminId());
        admin.setAdminLevel(dto.getAdminLevel() != null ? dto.getAdminLevel() : 1);
        admin.setDepartment(dto.getDepartment());
        admin.setActive(true);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);

        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public Admin updateAdmin(Long id, AdminDTO dto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found with id: " + id));

        admin.setEmail(dto.getEmail());
        admin.setFirstName(dto.getFirstName());
        admin.setLastName(dto.getLastName());
        admin.setPhoneNumber(dto.getPhoneNumber());
        admin.setProfilePicture(dto.getProfilePicture());
        admin.setAdminLevel(dto.getAdminLevel());
        admin.setDepartment(dto.getDepartment());

        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }

    @Override
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> getAdminByAdminId(String adminId) {
        return adminRepository.findByAdminId(adminId);
    }

    @Override
    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public AdminDTO convertToDTO(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setProfilePicture(admin.getProfilePicture());
        dto.setAdminId(admin.getAdminId());
        dto.setAdminLevel(admin.getAdminLevel());
        dto.setDepartment(admin.getDepartment());
        dto.setActive(admin.isActive());
        return dto;
    }
}
