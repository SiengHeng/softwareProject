package com.university.courseenrollment.demogradle.service.auth;

import com.university.courseenrollment.demogradle.dto.LoginRequest;
import com.university.courseenrollment.demogradle.dto.RegistrationRequest;
import com.university.courseenrollment.demogradle.model.entity.User;

public interface AuthenticationService {
    User login(LoginRequest request);
    User register(RegistrationRequest request);
    void logout();
    User getCurrentUser();
    boolean isAuthenticated();
}
