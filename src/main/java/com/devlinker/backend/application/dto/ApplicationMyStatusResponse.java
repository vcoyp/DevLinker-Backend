package com.devlinker.backend.application.dto;

import com.devlinker.backend.application.entity.Application;
import com.devlinker.backend.application.entity.ApplicationStatus;

public class ApplicationMyStatusResponse {

    private final boolean applied;
    private final Long applicationId;
    private final ApplicationStatus status;

    private ApplicationMyStatusResponse(
            boolean applied,
            Long applicationId,
            ApplicationStatus status
    ) {
        this.applied = applied;
        this.applicationId = applicationId;
        this.status = status;
    }

    public static ApplicationMyStatusResponse notApplied() {
        return new ApplicationMyStatusResponse(false, null, null);
    }

    public static ApplicationMyStatusResponse from(Application application) {
        return new ApplicationMyStatusResponse(
                true,
                application.getId(),
                application.getStatus()
        );
    }

    public boolean isApplied() {
        return applied;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }
}