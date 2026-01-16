package com.university.courseenrollment.demogradle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.university.courseenrollment.demogradle.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
