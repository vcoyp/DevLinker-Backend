package com.devlinker.backend.team.dto;

import com.devlinker.backend.team.entity.TeamMember;
import com.devlinker.backend.team.entity.TeamMemberRole;

public class TeamResponse {

    private Long teamId;
    private String teamName;
    private Long postId;
    private String postTitle;
    private TeamMemberRole role;

    private TeamResponse(
            Long teamId,
            String teamName,
            Long postId,
            String postTitle,
            TeamMemberRole role
    ) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.postId = postId;
        this.postTitle = postTitle;
        this.role = role;
    }

    public static TeamResponse from(TeamMember teamMember) {
        return new TeamResponse(
                teamMember.getTeam().getId(),
                teamMember.getTeam().getName(),
                teamMember.getTeam().getPost().getId(),
                teamMember.getTeam().getPost().getTitle(),
                teamMember.getRole()
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

    public TeamMemberRole getRole() {
        return role;
    }
}