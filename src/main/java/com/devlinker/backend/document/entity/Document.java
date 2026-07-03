package com.devlinker.backend.document.entity;

import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어느 팀의 문서인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 문서 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Document() {
    }

    private Document(
            Team team,
            User writer,
            String title,
            String content
    ) {
        this.team = team;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public static Document create(
            Team team,
            User writer,
            String title,
            String content
    ) {
        return new Document(team, writer, title, content);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}