package com.devlinker.backend.schedule.dto;

import java.time.LocalDateTime;

public class ScheduleCreateRequest {

    private String title;
    private String content;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

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
}