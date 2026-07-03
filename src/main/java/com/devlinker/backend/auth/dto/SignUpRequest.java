package com.devlinker.backend.auth.dto;

public class SignUpRequest {

    private String email;
    private String password;
    private String nickname;

    public SignUpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}