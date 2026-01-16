package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAdminId(String adminId);
    Optional<Admin> findByUsername(String username);
    boolean existsByAdminId(String adminId);
}
