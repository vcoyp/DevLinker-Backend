package com.devlinker.backend.team.dto;

import com.devlinker.backend.notice.entity.Notice;

import java.time.LocalDateTime;

public class TeamDashboardNoticeResponse {

    private final Long noticeId;
    private final String title;
    private final Long writerId;
    private final String writerNickname;
    private final LocalDateTime createdAt;

    private TeamDashboardNoticeResponse(
            Long noticeId,
            String title,
            Long writerId,
            String writerNickname,
            LocalDateTime createdAt
    ) {
        this.noticeId = noticeId;
        this.title = title;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.createdAt = createdAt;
    }

    public static TeamDashboardNoticeResponse from(Notice notice) {
        return new TeamDashboardNoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getWriter().getId(),
                notice.getWriter().getNickname(),
                notice.getCreatedAt()
        );
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public String getTitle() {
        return title;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getWriterNickname() {
        return writerNickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}