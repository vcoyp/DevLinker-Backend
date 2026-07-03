package com.devlinker.backend.schedule.dto;

import com.devlinker.backend.schedule.entity.Schedule;

import java.time.LocalDateTime;

public class ScheduleResponse {

    private final Long scheduleId;
    private final Long teamId;
    private final Long writerId;
    private final String writerNickname;
    private final String title;
    private final String content;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final LocalDateTime createdAt;

    private ScheduleResponse(
            Long scheduleId,
            Long teamId,
            Long writerId,
            String writerNickname,
            String title,
            String content,
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime createdAt
    ) {
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.title = title;
        this.content = content;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
    }

    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTeam().getId(),
                schedule.getWriter().getId(),
                schedule.getWriter().getNickname(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getCreatedAt()
        );
    }

    public Long getScheduleId() {
        return scheduleId;
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