package com.devlinker.backend.auth.entity;

import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    protected RefreshToken() {
    }

    public RefreshToken(User user, String refreshToken, LocalDateTime expiryAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiryAt = expiryAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public LocalDateTime getExpiryAt() {
        return expiryAt;
    }

    public void updateToken(String refreshToken, LocalDateTime expiryAt) {
        this.refreshToken = refreshToken;
        this.expiryAt = expiryAt;
    }
}
