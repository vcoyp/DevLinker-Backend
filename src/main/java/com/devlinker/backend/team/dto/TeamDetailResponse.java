package com.devlinker.backend.team.dto;

import com.devlinker.backend.team.entity.Team;

import java.time.LocalDateTime;

public class TeamDetailResponse {

    private Long teamId;
    private String teamName;
    private Long postId;
    private String postTitle;
    private LocalDateTime createdAt;

    private TeamDetailResponse(
            Long teamId,
            String teamName,
            Long postId,
            String postTitle,
            LocalDateTime createdAt
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.postId = postId;
        this.postTitle = postTitle;
        this.createdAt = createdAt;
    }

    public static TeamDetailResponse from(Team team) {
        return new TeamDetailResponse(
                team.getId(),
                team.getName(),
                team.getPost().getId(),
                team.getPost().getTitle(),
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

    public String getPostTitle() {
        return postTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}