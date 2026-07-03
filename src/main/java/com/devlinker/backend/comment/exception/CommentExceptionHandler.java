package com.devlinker.backend.comment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.devlinker.backend.comment")
public class CommentExceptionHandler {

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<Map<String, Object>> handleCommentException(CommentException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("code", e.getCode());
        body.put("message", e.getMessage());
        body.put("data", null);

        return ResponseEntity.status(e.getStatus()).body(body);
    }
}