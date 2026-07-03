package com.devlinker.backend.comment.controller;

import com.devlinker.backend.comment.dto.CommentCreateRequest;
import com.devlinker.backend.comment.dto.CommentResponse;
import com.devlinker.backend.comment.exception.CommentException;
import com.devlinker.backend.comment.service.CommentService;
import com.devlinker.backend.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request,
            Principal principal
    ) {
        String email = getLoginEmail(principal);

        CommentResponse response = commentService.createComment(email, postId, request);

        return ApiResponse.ok("댓글 작성 성공", response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        List<CommentResponse> responses = commentService.getComments(postId);

        return ApiResponse.ok("댓글 목록 조회 성공", responses);
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            Principal principal
    ) {
        String email = getLoginEmail(principal);

        commentService.deleteComment(email, commentId);

        return ApiResponse.ok("댓글 삭제 성공", null);
    }

    private String getLoginEmail(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new CommentException(
                    HttpStatus.UNAUTHORIZED,
                    "COMMON401",
                    "로그인이 필요합니다."
            );
        }

        return principal.getName();
    }
}