package com.university.courseenrollment.demogradle.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_token", nullable = false, unique = true, length = 500)
    private String sessionToken;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;
}
