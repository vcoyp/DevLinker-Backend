package com.devlinker.backend.team.controller;

import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.team.dto.TeamDashboardResponse;
import com.devlinker.backend.team.dto.TeamDetailResponse;
import com.devlinker.backend.team.dto.TeamMemberResponse;
import com.devlinker.backend.team.dto.TeamResponse;
import com.devlinker.backend.team.service.TeamService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/api/teams/my")
    public ApiResponse<List<TeamResponse>> getMyTeams(Authentication authentication) {
        String email = getLoginEmail(authentication);
        List<TeamResponse> response = teamService.getMyTeams(email);

        return ApiResponse.ok("내 팀 목록 조회 성공", response);
    }

    @GetMapping("/api/teams/{teamId}/dashboard")
    public ApiResponse<TeamDashboardResponse> getTeamDashboard(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        TeamDashboardResponse response = teamService.getTeamDashboard(email, teamId);

        return ApiResponse.ok("팀 대시보드 조회 성공", response);
    }

    @GetMapping("/api/teams/{teamId}")
    public ApiResponse<TeamDetailResponse> getTeamDetail(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        TeamDetailResponse response = teamService.getTeamDetail(email, teamId);

        return ApiResponse.ok("팀 상세 조회 성공", response);
    }

    @GetMapping("/api/teams/{teamId}/members")
    public ApiResponse<List<TeamMemberResponse>> getTeamMembers(
            @PathVariable Long teamId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);
        List<TeamMemberResponse> response = teamService.getTeamMembers(email, teamId);

        return ApiResponse.ok("팀 멤버 목록 조회 성공", response);
    }

    private String getLoginEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return authentication.getName();
    }
}