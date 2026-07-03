package com.devlinker.backend.retrospective.dto;

import com.devlinker.backend.retrospective.entity.Retrospective;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RetrospectiveResponse {

    private Long id;
    private Long teamId;
    private Long writerId;
    private String writerNickname;
    private String title;
    private String goodPoint;
    private String problemPoint;
    private String improvementPoint;
    private LocalDateTime createdAt;

    public static RetrospectiveResponse from(Retrospective retrospective) {
        return new RetrospectiveResponse(
                retrospective.getId(),
                retrospective.getTeam().getId(),
                retrospective.getWriter().getId(),
                retrospective.getWriter().getNickname(),
                retrospective.getTitle(),
                retrospective.getGoodPoint(),
                retrospective.getProblemPoint(),
                retrospective.getImprovementPoint(),
                retrospective.getCreatedAt()
        );
    }
}