package com.devlinker.backend.retrospective.entity;

import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Retrospective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 팀의 회고인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 누가 작성했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String goodPoint;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String problemPoint;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String improvementPoint;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Retrospective(
            Team team,
            User writer,
            String title,
            String goodPoint,
            String problemPoint,
            String improvementPoint
    ) {
        this.team = team;
        this.writer = writer;
        this.title = title;
        this.goodPoint = goodPoint;
        this.problemPoint = problemPoint;
        this.improvementPoint = improvementPoint;
        this.createdAt = LocalDateTime.now();
    }

    public static Retrospective create(
            Team team,
            User writer,
            String title,
            String goodPoint,
            String problemPoint,
            String improvementPoint
    ) {
        return new Retrospective(team, writer, title, goodPoint, problemPoint, improvementPoint);
    }
}