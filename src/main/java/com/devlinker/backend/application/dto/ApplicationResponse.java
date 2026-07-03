package com.devlinker.backend.application.dto;

import com.devlinker.backend.application.entity.Application;
import com.devlinker.backend.application.entity.ApplicationStatus;

import java.time.LocalDateTime;

public class ApplicationResponse {

    private Long id;
    private Long postId;
    private String postTitle;
    private Long applicantId;
    private String applicantNickname;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ApplicationResponse(
            Long id,
            Long postId,
            String postTitle,
            Long applicantId,
            String applicantNickname,
            ApplicationStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.postId = postId;
        this.postTitle = postTitle;
        this.applicantId = applicantId;
        this.applicantNickname = applicantNickname;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getPost().getId(),
                application.getPost().getTitle(),
                application.getApplicant().getId(),
                application.getApplicant().getNickname(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public String getApplicantNickname() {
        return applicantNickname;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}