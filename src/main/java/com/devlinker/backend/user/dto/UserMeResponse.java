package com.devlinker.backend.user.dto;

import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.entity.UserRole;
import com.devlinker.backend.user.entity.UserStatus;

public class UserMeResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole role;
    private final UserStatus status;

    public UserMeResponse(
            Long id,
            String email,
            String nickname,
            UserRole role,
            UserStatus status
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    public static UserMeResponse from(User user) {
        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }
}