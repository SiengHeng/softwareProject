package com.university.courseenrollment.demogradle.repository;

import com.university.courseenrollment.demogradle.model.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId);
    List<LoginHistory> findByUsernameOrderByLoginTimeDesc(String username);
    List<LoginHistory> findByLoginStatus(String loginStatus);
    List<LoginHistory> findByLoginTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByUserIdAndLoginStatus(Long userId, String loginStatus);
}
