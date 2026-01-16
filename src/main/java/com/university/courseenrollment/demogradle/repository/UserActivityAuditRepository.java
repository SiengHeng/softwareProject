package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.UserActivityAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface UserActivityAuditRepository extends JpaRepository<UserActivityAudit, Long> {
    List<UserActivityAudit> findByUserIdOrderByTimestampDesc(Long userId);
    List<UserActivityAudit> findByAction(String action);
    List<UserActivityAudit> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<UserActivityAudit> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<UserActivityAudit> findTop100ByOrderByTimestampDesc();
}
