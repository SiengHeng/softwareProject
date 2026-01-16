package com.university.courseenrollment.demogradle.service.session;

import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.model.entity.UserSession;
import com.university.courseenrollment.demogradle.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserSessionRepository sessionRepository;
    private static final int SESSION_TIMEOUT_HOURS = 24;

    @Override
    @Transactional
    public UserSession createSession(User user, HttpServletRequest request) {
        try {
            UserSession session = new UserSession();
            session.setUser(user);
            session.setSessionToken(UUID.randomUUID().toString());
            session.setIpAddress(getClientIpAddress(request));
            session.setUserAgent(request.getHeader("User-Agent"));
            session.setLoginTime(LocalDateTime.now());
            session.setLastActivity(LocalDateTime.now());
            session.setExpiryTime(LocalDateTime.now().plusHours(SESSION_TIMEOUT_HOURS));
            session.setActive(true);
            
            UserSession saved = sessionRepository.save(session);
            log.info("Session created for user: {} with token: {}", user.getUsername(), session.getSessionToken());
            return saved;
        } catch (Exception e) {
            log.error("Error creating session for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to create session", e);
        }
    }

    @Override
    @Transactional
    public void updateSessionActivity(String sessionToken) {
        try {
            sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
                session.setLastActivity(LocalDateTime.now());
                sessionRepository.save(session);
            });
        } catch (Exception e) {
            log.error("Error updating session activity for token: {}", sessionToken, e);
        }
    }

    @Override
    @Transactional
    public void invalidateSession(String sessionToken) {
        try {
            sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());
                sessionRepository.save(session);
                log.info("Session invalidated: {}", sessionToken);
            });
        } catch (Exception e) {
            log.error("Error invalidating session: {}", sessionToken, e);
        }
    }

    @Override
    @Transactional
    public void invalidateAllUserSessions(Long userId) {
        try {
            List<UserSession> sessions = sessionRepository.findByUserIdAndActiveTrue(userId);
            sessions.forEach(session -> {
                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());
            });
            sessionRepository.saveAll(sessions);
            log.info("All sessions invalidated for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error invalidating all sessions for user ID: {}", userId, e);
        }
    }

    @Override
    public Optional<UserSession> getSession(String sessionToken) {
        return sessionRepository.findBySessionToken(sessionToken);
    }

    @Override
    public List<UserSession> getActiveSessions(Long userId) {
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    @Transactional
    public void cleanupExpiredSessions() {
        try {
            sessionRepository.deleteByExpiryTimeBefore(LocalDateTime.now());
            log.info("Expired sessions cleaned up");
        } catch (Exception e) {
            log.error("Error cleaning up expired sessions", e);
        }
    }

    @Override
    public boolean isSessionValid(String sessionToken) {
        Optional<UserSession> session = sessionRepository.findBySessionToken(sessionToken);
        if (session.isEmpty()) {
            return false;
        }
        
        UserSession userSession = session.get();
        return userSession.isActive() && 
               userSession.getExpiryTime().isAfter(LocalDateTime.now());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", 
                           "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_FORWARDED_FOR", 
                           "HTTP_FORWARDED", "HTTP_CLIENT_IP", "HTTP_VIA", "REMOTE_ADDR"};
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
