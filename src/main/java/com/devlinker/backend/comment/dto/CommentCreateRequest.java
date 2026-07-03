package com.devlinker.backend.comment.dto;

public class CommentCreateRequest {

    private String content;

    public CommentCreateRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}