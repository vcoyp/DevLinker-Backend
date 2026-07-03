package com.devlinker.backend.post.entity;

import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 모집글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 3000)
    private String content;

    @Column(nullable = false)
    private Integer recruitCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status;

    // 조회수
    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Post() {
    }

    public Post(User user, String title, String content, Integer recruitCount, PostStatus status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.recruitCount = recruitCount;
        this.status = status;
        this.viewCount = 0L;
    }

    public static Post create(User user, String title, String content, Integer recruitCount) {
        return new Post(user, title, content, recruitCount, PostStatus.OPEN);
    }


    public void update(String title, String content, Integer recruitCount) {
        this.title = title;
        this.content = content;
        this.recruitCount = recruitCount;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.viewCount == null) {
            this.viewCount = 0L;
        }
    }

    public void update(String title, String content, Integer recruitCount, PostStatus status) {
        this.title = title;
        this.content = content;
        this.recruitCount = recruitCount;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0L;
        }

        this.viewCount++;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getRecruitCount() {
        return recruitCount;
    }

    public PostStatus getStatus() {
        return status;
    }

    public Long getViewCount() {
        if (viewCount == null) {
            return 0L;
        }

        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}