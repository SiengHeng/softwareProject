package com.university.courseenrollment.demogradle.service.audit;

import com.university.courseenrollment.demogradle.model.entity.LoginHistory;
import com.university.courseenrollment.demogradle.model.entity.User;
import com.university.courseenrollment.demogradle.model.entity.UserActivityAudit;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface AuditService {
    // Login history
    void recordLogin(User user, HttpServletRequest request, String status);
    void recordLogout(User user, Long loginHistoryId);
    List<LoginHistory> getLoginHistory(Long userId);
    List<LoginHistory> getRecentLogins(int limit);
    
    // Activity audit
    void logActivity(String action, String entityType, Long entityId, String description);
    void logActivity(User user, String action, String entityType, Long entityId, String oldValue, String newValue, String description);
    List<UserActivityAudit> getUserActivities(Long userId);
    List<UserActivityAudit> getRecentActivities(int limit);
    
    // Statistics
    long getFailedLoginCount(Long userId);
    long getTotalLoginCount(Long userId);
}
