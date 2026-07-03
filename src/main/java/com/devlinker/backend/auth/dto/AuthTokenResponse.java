package com.devlinker.backend.auth.dto;

public class AuthTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}