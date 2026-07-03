package com.devlinker.backend.notice.dto;

import com.devlinker.backend.notice.entity.Notice;

import java.time.LocalDateTime;

public class NoticeResponse {

    private Long noticeId;
    private Long teamId;
    private String teamName;
    private Long writerId;
    private String writerNickname;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private NoticeResponse(
            Long noticeId,
            Long teamId,
            String teamName,
            Long writerId,
            String writerNickname,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.noticeId = noticeId;
        this.teamId = teamId;
        this.teamName = teamName;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTeam().getId(),
                notice.getTeam().getName(),
                notice.getWriter().getId(),
                notice.getWriter().getNickname(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}