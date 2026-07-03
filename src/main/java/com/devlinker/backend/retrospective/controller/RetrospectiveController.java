package com.devlinker.backend.retrospective.controller;

import com.devlinker.backend.auth.security.CustomUserDetails;
import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.retrospective.dto.RetrospectiveCreateRequest;
import com.devlinker.backend.retrospective.dto.RetrospectiveResponse;
import com.devlinker.backend.retrospective.service.RetrospectiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RetrospectiveController {

    private final RetrospectiveService retrospectiveService;

    @PostMapping("/teams/{teamId}/retrospectives")
    public ApiResponse<RetrospectiveResponse> createRetrospective(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody RetrospectiveCreateRequest request
    ) {
        Long userId = userDetails.getId();

        RetrospectiveResponse response = retrospectiveService.createRetrospective(
                teamId,
                userId,
                request
        );

        return ApiResponse.ok("Retrospective created successfully.", response);
    }

    @GetMapping("/teams/{teamId}/retrospectives")
    public ApiResponse<List<RetrospectiveResponse>> getRetrospectives(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();

        List<RetrospectiveResponse> response = retrospectiveService.getRetrospectives(
                teamId,
                userId
        );

        return ApiResponse.ok("Retrospective list retrieved successfully.", response);
    }

    @GetMapping("/retrospectives/{retrospectiveId}")
    public ApiResponse<RetrospectiveResponse> getRetrospective(
            @PathVariable Long retrospectiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();

        RetrospectiveResponse response = retrospectiveService.getRetrospective(
                retrospectiveId,
                userId
        );

        return ApiResponse.ok("Retrospective detail retrieved successfully.", response);
    }

    @DeleteMapping("/retrospectives/{retrospectiveId}")
    public ApiResponse<Void> deleteRetrospective(
            @PathVariable Long retrospectiveId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();

        retrospectiveService.deleteRetrospective(retrospectiveId, userId);

        return ApiResponse.ok("Retrospective deleted successfully.", null);
    }
}