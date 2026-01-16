package com.university.courseenrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.courseenrollment.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
