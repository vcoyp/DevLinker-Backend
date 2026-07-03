package com.devlinker.backend.comment.dto;

import com.devlinker.backend.comment.entity.Comment;

import java.time.LocalDateTime;

public class CommentResponse {

    private final Long id;
    private final Long postId;
    private final Long writerId;
    private final String writerNickname;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private CommentResponse(
            Long id,
            Long postId,
            Long writerId,
            String writerNickname,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.postId = postId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getWriterNickname() {
        return writerNickname;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}