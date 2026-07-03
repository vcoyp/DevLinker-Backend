package com.devlinker.backend.team.dto;

import com.devlinker.backend.team.entity.TeamMember;
import com.devlinker.backend.team.entity.TeamMemberRole;

import java.time.LocalDateTime;

public class TeamMemberResponse {

    private Long memberId;
    private Long userId;
    private String nickname;
    private TeamMemberRole role;
    private LocalDateTime joinedAt;

    private TeamMemberResponse(
            Long memberId,
            Long userId,
            String nickname,
            TeamMemberRole role,
            LocalDateTime joinedAt
    ) {
        this.memberId = memberId;
        this.userId = userId;
        this.nickname = nickname;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public static TeamMemberResponse from(TeamMember teamMember) {
        return new TeamMemberResponse(
                teamMember.getId(),
                teamMember.getUser().getId(),
                teamMember.getUser().getNickname(),
                teamMember.getRole(),
                teamMember.getCreatedAt()
        );
    }

    public Long getMemberId() {
        return memberId;
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

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}