package com.devlinker.backend.schedule.entity;

import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 팀의 일정인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 일정 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Schedule() {
    }

    private Schedule(
            Team team,
            User writer,
            String title,
            String content,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        this.team = team;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
    }

    public static Schedule create(
            Team team,
            User writer,
            String title,
            String content,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return new Schedule(team, writer, title, content, startAt, endAt);
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public User getWriter() {
        return writer;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}