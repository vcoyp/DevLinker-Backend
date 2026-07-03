package com.devlinker.backend.team.dto;

import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.entity.TeamMemberRole;

import java.time.LocalDateTime;
import java.util.List;

public class TeamDashboardResponse {

    private final Long teamId;
    private final String teamName;
    private final Long postId;
    private final TeamMemberRole myRole;
    private final List<TeamDashboardMemberResponse> members;
    private final List<TeamDashboardNoticeResponse> recentNotices;
    private final LocalDateTime createdAt;

    private TeamDashboardResponse(
            Long teamId,
            String teamName,
            Long postId,
            TeamMemberRole myRole,
            List<TeamDashboardMemberResponse> members,
            List<TeamDashboardNoticeResponse> recentNotices,
            LocalDateTime createdAt
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.postId = postId;
        this.myRole = myRole;
        this.members = members;
        this.recentNotices = recentNotices;
        this.createdAt = createdAt;
    }

    public static TeamDashboardResponse of(
            Team team,
            TeamMemberRole myRole,
            List<TeamDashboardMemberResponse> members,
            List<TeamDashboardNoticeResponse> recentNotices
    ) {
        Long postId = team.getPost() != null ? team.getPost().getId() : null;

        return new TeamDashboardResponse(
                team.getId(),
                team.getName(),
                postId,
                myRole,
                members,
                recentNotices,
                team.getCreatedAt()
        );
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Long getPostId() {
        return postId;
    }

    public TeamMemberRole getMyRole() {
        return myRole;
    }

    public List<TeamDashboardMemberResponse> getMembers() {
        return members;
    }

    public List<TeamDashboardNoticeResponse> getRecentNotices() {
        return recentNotices;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}