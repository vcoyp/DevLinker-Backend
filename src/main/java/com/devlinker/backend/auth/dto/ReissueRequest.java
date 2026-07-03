package com.devlinker.backend.auth.dto;

public class ReissueRequest {

    private String refreshToken;

    public ReissueRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}