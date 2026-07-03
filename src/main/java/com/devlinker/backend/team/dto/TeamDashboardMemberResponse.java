package com.devlinker.backend.team.dto;

import com.devlinker.backend.team.entity.TeamMember;
import com.devlinker.backend.team.entity.TeamMemberRole;

public class TeamDashboardMemberResponse {

    private final Long userId;
    private final String nickname;
    private final TeamMemberRole role;

    private TeamDashboardMemberResponse(
            Long userId,
            String nickname,
            TeamMemberRole role
    ) {
        this.userId = userId;
        this.nickname = nickname;
        this.role = role;
    }

    public static TeamDashboardMemberResponse from(TeamMember teamMember) {
        return new TeamDashboardMemberResponse(
                teamMember.getUser().getId(),
                teamMember.getUser().getNickname(),
                teamMember.getRole()
        );
    }

    public Long getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public TeamMemberRole getRole() {
        return role;
    }
}