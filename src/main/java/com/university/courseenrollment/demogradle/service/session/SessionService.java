package com.university.courseenrollment.demogradle.service.session;

import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.model.entity.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface SessionService {
    UserSession createSession(User user, HttpServletRequest request);
    void updateSessionActivity(String sessionToken);
    void invalidateSession(String sessionToken);
    void invalidateAllUserSessions(Long userId);
    Optional<UserSession> getSession(String sessionToken);
    List<UserSession> getActiveSessions(Long userId);
    void cleanupExpiredSessions();
    boolean isSessionValid(String sessionToken);
}
