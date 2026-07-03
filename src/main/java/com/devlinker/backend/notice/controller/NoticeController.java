package com.devlinker.backend.notice.controller;

import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.notice.dto.NoticeCreateRequest;
import com.devlinker.backend.notice.dto.NoticeResponse;
import com.devlinker.backend.notice.dto.NoticeUpdateRequest;
import com.devlinker.backend.notice.service.NoticeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // 공지 작성
    @PostMapping("/api/teams/{teamId}/notices")
    public ApiResponse<NoticeResponse> createNotice(
            @PathVariable Long teamId,
            @RequestBody NoticeCreateRequest request,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        NoticeResponse response = noticeService.createNotice(email, teamId, request);

        return ApiResponse.ok("공지 작성 성공", response);
    }

    // 팀별 공지 목록 조회
    @GetMapping("/api/teams/{teamId}/notices")
    public ApiResponse<List<NoticeResponse>> getNotices(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        List<NoticeResponse> response = noticeService.getNotices(email, teamId);

        return ApiResponse.ok("공지 목록 조회 성공", response);
    }

    // 공지 상세 조회
    @GetMapping("/api/notices/{noticeId}")
    public ApiResponse<NoticeResponse> getNoticeDetail(
            @PathVariable Long noticeId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        NoticeResponse response = noticeService.getNoticeDetail(email, noticeId);

        return ApiResponse.ok("공지 상세 조회 성공", response);
    }

    // 공지 수정
    @PatchMapping("/api/notices/{noticeId}")
    public ApiResponse<NoticeResponse> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequest request,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        NoticeResponse response = noticeService.updateNotice(email, noticeId, request);

        return ApiResponse.ok("공지 수정 성공", response);
    }

    // 공지 삭제
    @DeleteMapping("/api/notices/{noticeId}")
    public ApiResponse<Void> deleteNotice(
            @PathVariable Long noticeId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        noticeService.deleteNotice(email, noticeId);

        return ApiResponse.ok("공지 삭제 성공", null);
    }

    private String getLoginEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return authentication.getName();
    }
}