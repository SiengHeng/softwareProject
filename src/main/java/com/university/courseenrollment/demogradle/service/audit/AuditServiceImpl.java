package com.university.courseenrollment.demogradle.service.audit;

import com.university.courseenrollment.demogradle.model.entity.LoginHistory;
import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.model.entity.UserActivityAudit;
import com.university.courseenrollment.demogradle.repository.LoginHistoryRepository;
import com.university.courseenrollment.demogradle.repository.UserActivityAuditRepository;
import com.university.courseenrollment.demogradle.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserActivityAuditRepository activityAuditRepository;

    @Override
    @Transactional
    public void recordLogin(User user, HttpServletRequest request, String status) {
        try {
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setUsername(user.getUsername());
            loginHistory.setLoginTime(LocalDateTime.now());
            loginHistory.setIpAddress(getClientIpAddress(request));
            loginHistory.setUserAgent(request.getHeader("User-Agent"));
            loginHistory.setBrowser(parseBrowser(request.getHeader("User-Agent")));
            loginHistory.setOperatingSystem(parseOS(request.getHeader("User-Agent")));
            loginHistory.setDeviceType(parseDeviceType(request.getHeader("User-Agent")));
            loginHistory.setLoginStatus(status);
            
            loginHistoryRepository.save(loginHistory);
            log.info("Login recorded for user: {} with status: {}", user.getUsername(), status);
        } catch (Exception e) {
            log.error("Error recording login for user: {}", user.getUsername(), e);
        }
    }

    @Override
    @Transactional
    public void recordLogout(User user, Long loginHistoryId) {
        try {
            if (loginHistoryId != null) {
                loginHistoryRepository.findById(loginHistoryId).ifPresent(history -> {
                    history.setLogoutTime(LocalDateTime.now());
                    if (history.getLoginTime() != null) {
                        long duration = java.time.Duration.between(history.getLoginTime(), LocalDateTime.now()).getSeconds();
                        history.setSessionDuration((int) duration);
                    }
                    loginHistoryRepository.save(history);
                });
            }
            log.info("Logout recorded for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error recording logout for user: {}", user.getUsername(), e);
        }
    }

    @Override
    public List<LoginHistory> getLoginHistory(Long userId) {
        return loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    @Override
    public List<LoginHistory> getRecentLogins(int limit) {
        return loginHistoryRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    @Override
    @Transactional
    public void logActivity(String action, String entityType, Long entityId, String description) {
        try {
            UserActivityAudit audit = new UserActivityAudit();
            
            SecurityUtils.getCurrentUsername().ifPresent(username -> {
                audit.setUsername(username);
            });
            
            audit.setAction(action);
            audit.setEntityType(entityType);
            audit.setEntityId(entityId);
            audit.setDescription(description);
            audit.setTimestamp(LocalDateTime.now());
            
            // Try to get IP address from request
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                audit.setIpAddress(getClientIpAddress(request));
            }
            
            activityAuditRepository.save(audit);
            log.debug("Activity logged: {} - {} - {}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Error logging activity: {} - {}", action, entityType, e);
        }
    }

    @Override
    @Transactional
    public void logActivity(User user, String action, String entityType, Long entityId, 
                          String oldValue, String newValue, String description) {
        try {
            UserActivityAudit audit = new UserActivityAudit();
            audit.setUser(user);
            audit.setUsername(user.getUsername());
            audit.setAction(action);
            audit.setEntityType(entityType);
            audit.setEntityId(entityId);
            audit.setOldValue(oldValue);
            audit.setNewValue(newValue);
            audit.setDescription(description);
            audit.setTimestamp(LocalDateTime.now());
            
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                audit.setIpAddress(getClientIpAddress(request));
            }
            
            activityAuditRepository.save(audit);
            log.debug("Activity logged for user {}: {} - {} - {}", user.getUsername(), action, entityType, entityId);
        } catch (Exception e) {
            log.error("Error logging activity for user {}: {} - {}", user.getUsername(), action, entityType, e);
        }
    }

    @Override
    public List<UserActivityAudit> getUserActivities(Long userId) {
        return activityAuditRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    @Override
    public List<UserActivityAudit> getRecentActivities(int limit) {
        return activityAuditRepository.findTop100ByOrderByTimestampDesc();
    }

    @Override
    public long getFailedLoginCount(Long userId) {
        return loginHistoryRepository.countByUserIdAndLoginStatus(userId, "FAILED");
    }

    @Override
    public long getTotalLoginCount(Long userId) {
        return loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId).size();
    }

    // Helper methods
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

    private String parseBrowser(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Opera")) return "Opera";
        return "Other";
    }

    private String parseOS(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iOS")) return "iOS";
        return "Other";
    }

    private String parseDeviceType(String userAgent) {
        if (userAgent == null) return "Unknown";
        if (userAgent.contains("Mobile")) return "Mobile";
        if (userAgent.contains("Tablet")) return "Tablet";
        return "Desktop";
    }
}
