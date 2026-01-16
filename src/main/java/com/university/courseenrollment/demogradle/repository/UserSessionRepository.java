package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(String sessionToken);
    List<UserSession> findByUserIdAndActiveTrue(Long userId);
    List<UserSession> findByUserId(Long userId);
    void deleteByExpiryTimeBefore(LocalDateTime dateTime);
    long countByUserIdAndActiveTrue(Long userId);
}
