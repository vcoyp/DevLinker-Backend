package com.devlinker.backend.document.dto;

import com.devlinker.backend.document.entity.Document;

import java.time.LocalDateTime;

public class DocumentResponse {

    private final Long documentId;
    private final Long teamId;
    private final Long writerId;
    private final String writerNickname;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    private DocumentResponse(
            Long documentId,
            Long teamId,
            Long writerId,
            String writerNickname,
            String title,
            String content,
            LocalDateTime createdAt
    ) {
        this.documentId = documentId;
        this.teamId = teamId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTeam().getId(),
                document.getWriter().getId(),
                document.getWriter().getNickname(),
                document.getTitle(),
                document.getContent(),
                document.getCreatedAt()
        );
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Long getTeamId() {
        return teamId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}