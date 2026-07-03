package com.devlinker.backend.post.dto;

import com.devlinker.backend.post.entity.PostStatus;

public class PostUpdateRequest {

    private String title;
    private String content;
    private Integer recruitCount;
    private PostStatus status;

    public PostUpdateRequest() {
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

    public PostStatus getStatus() {
        return status;
    }
}