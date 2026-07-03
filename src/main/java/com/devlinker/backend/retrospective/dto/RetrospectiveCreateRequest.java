package com.devlinker.backend.retrospective.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RetrospectiveCreateRequest {

    private String title;
    private String goodPoint;
    private String problemPoint;
    private String improvementPoint;
}