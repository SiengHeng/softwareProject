package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.dto.AdminDTO;
import com.university.courseenrollment.demogradle.model.entity.Admin;
import java.util.List;
import java.util.Optional;
public interface AdminService {
    Admin createAdmin(AdminDTO dto);
    Admin updateAdmin(Long id, AdminDTO dto);
    void deleteAdmin(Long id);
    Optional<Admin> getAdminById(Long id);
    Optional<Admin> getAdminByAdminId(String adminId);
    Optional<Admin> getAdminByUsername(String username);
    List<Admin> getAllAdmins();
    AdminDTO convertToDTO(Admin admin);
}