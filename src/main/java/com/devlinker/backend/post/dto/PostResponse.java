package com.devlinker.backend.post.dto;

import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.entity.PostStatus;

import java.time.LocalDateTime;

public class PostResponse {

    private Long id;

    private Long writerId;
    private String writerNickname;

    private String title;
    private String content;
    private Integer recruitCount;
    private PostStatus status;

    private Long viewCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PostResponse(
            Long id,
            Long writerId,
            String writerNickname,
            String title,
            String content,
            Integer recruitCount,
            PostStatus status,
            Long viewCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.title = title;
        this.content = content;
        this.recruitCount = recruitCount;
        this.status = status;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getRecruitCount(),
                post.getStatus(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getWriterNickname() {
        return writerNickname;
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
        return viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}