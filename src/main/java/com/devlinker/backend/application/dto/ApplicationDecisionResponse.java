package com.devlinker.backend.application.dto;

import com.devlinker.backend.application.entity.ApplicationStatus;

public class ApplicationDecisionResponse {

    private Long applicationId;
    private ApplicationStatus status;
    private Long teamId;
    private String teamName;

    private ApplicationDecisionResponse(
            Long applicationId,
            ApplicationStatus status,
            Long teamId,
            String teamName
    ) {
        this.applicationId = applicationId;
        this.status = status;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public static ApplicationDecisionResponse approve(
            Long applicationId,
            ApplicationStatus status,
            Long teamId,
            String teamName
    ) {
        return new ApplicationDecisionResponse(applicationId, status, teamId, teamName);
    }

    public static ApplicationDecisionResponse reject(
            Long applicationId,
            ApplicationStatus status
    ) {
        return new ApplicationDecisionResponse(applicationId, status, null, null);
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }
}