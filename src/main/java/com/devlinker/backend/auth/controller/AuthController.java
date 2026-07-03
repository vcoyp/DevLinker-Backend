package com.devlinker.backend.auth.controller;

import com.devlinker.backend.auth.dto.AuthTokenResponse;
import com.devlinker.backend.auth.dto.LoginRequest;
import com.devlinker.backend.auth.dto.ReissueRequest;
import com.devlinker.backend.auth.dto.SignUpRequest;
import com.devlinker.backend.auth.security.CustomUserDetails;
import com.devlinker.backend.auth.service.AuthService;
import com.devlinker.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.ok("회원가입 성공", null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("로그인 성공", authService.login(request));
    }

    @PostMapping("/reissue")
    public ApiResponse<AuthTokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return ApiResponse.ok("토큰 재발급 성공", authService.reissue(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            authService.logout(userDetails.getEmail());
        }
        return ApiResponse.ok("로그아웃 성공", null);
    }
}