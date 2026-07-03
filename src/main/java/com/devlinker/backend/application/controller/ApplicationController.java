package com.devlinker.backend.application.controller;

import com.devlinker.backend.application.dto.ApplicationDecisionResponse;
import com.devlinker.backend.application.dto.ApplicationMyStatusResponse;
import com.devlinker.backend.application.dto.ApplicationResponse;
import com.devlinker.backend.application.service.ApplicationService;
import com.devlinker.backend.global.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // 지원하기
    @PostMapping("/api/posts/{postId}/applications")
    public ApiResponse<ApplicationResponse> createApplication(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        ApplicationResponse response = applicationService.createApplication(email, postId);

        return ApiResponse.ok("지원 성공", response);
    }

    // 내 지원 상태 조회
    @GetMapping("/api/posts/{postId}/applications/me")
    public ApiResponse<ApplicationMyStatusResponse> getMyApplicationStatus(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        ApplicationMyStatusResponse response = applicationService.getMyApplicationStatus(email, postId);

        return ApiResponse.ok("내 지원 상태 조회 성공", response);
    }

    // 지원자 목록 조회
    @GetMapping("/api/posts/{postId}/applications")
    public ApiResponse<List<ApplicationResponse>> getApplicationsByPost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        List<ApplicationResponse> response = applicationService.getApplicationsByPost(email, postId);

        return ApiResponse.ok("지원자 목록 조회 성공", response);
    }

    // 지원 취소
    @PatchMapping("/api/applications/{applicationId}/cancel")
    public ApiResponse<Void> cancelApplication(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        applicationService.cancelApplication(email, applicationId);

        return ApiResponse.ok("지원 취소 성공", null);
    }

    // 지원 승인
    @PatchMapping("/api/applications/{applicationId}/approve")
    public ApiResponse<ApplicationDecisionResponse> approveApplication(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        ApplicationDecisionResponse response = applicationService.approveApplication(email, applicationId);

        return ApiResponse.ok("지원 승인 성공", response);
    }

    // 지원 거절
    @PatchMapping("/api/applications/{applicationId}/reject")
    public ApiResponse<ApplicationDecisionResponse> rejectApplication(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        ApplicationDecisionResponse response = applicationService.rejectApplication(email, applicationId);

        return ApiResponse.ok("지원 거절 성공", response);
    }

    private String getLoginEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return authentication.getName();
    }
}