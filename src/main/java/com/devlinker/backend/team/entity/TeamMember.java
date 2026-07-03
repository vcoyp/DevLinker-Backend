package com.devlinker.backend.team.entity;

import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id", "user_id"})
        }
)
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 팀 소속인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 어떤 사용자인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamMemberRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected TeamMember() {
    }

    private TeamMember(Team team, User user, TeamMemberRole role) {
        this.team = team;
        this.user = user;
        this.role = role;
    }

    public static TeamMember create(Team team, User user, TeamMemberRole role) {
        return new TeamMember(team, user, role);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public User getUser() {
        return user;
    }

    public TeamMemberRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}