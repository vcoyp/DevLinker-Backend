package com.devlinker.backend.post.dto;

public class PostCreateRequest {

    private String title;
    private String content;
    private Integer recruitCount;

    public PostCreateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Integer getRecruitCount() {
        return recruitCount;
    }
}