package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.AuthenticationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long> {
    Optional<AuthenticationToken> findByToken(String token);
    List<AuthenticationToken> findByUserIdAndRevokedFalse(Long userId);
    List<AuthenticationToken> findByUserId(Long userId);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    long countByUserIdAndRevokedFalse(Long userId);
}
