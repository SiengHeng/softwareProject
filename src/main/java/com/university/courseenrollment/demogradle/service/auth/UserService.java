package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.model.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    List<User> searchUsers(String keyword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void toggleUserStatus(Long id);
}