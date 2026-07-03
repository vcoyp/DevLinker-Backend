package com.devlinker.backend.post.controller;

import com.devlinker.backend.global.response.ApiResponse;
import com.devlinker.backend.post.dto.PostCreateRequest;
import com.devlinker.backend.post.dto.PostResponse;
import com.devlinker.backend.post.dto.PostSearchType;
import com.devlinker.backend.post.dto.PostUpdateRequest;
import com.devlinker.backend.post.entity.PostStatus;
import com.devlinker.backend.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 모집글 작성: 로그인 사용자만 가능
    @PostMapping
    public ApiResponse<PostResponse> createPost(
            @RequestBody PostCreateRequest request,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);

        PostResponse response = postService.createPost(email, request);

        return ApiResponse.ok("모집글 작성 성공", response);
    }

    // 모집글 목록 조회: 공개 API + 검색/필터 + 페이징
    @GetMapping
    public ApiResponse<Page<PostResponse>> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false, defaultValue = "TITLE_CONTENT") PostSearchType searchType,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        Page<PostResponse> response = postService.getPosts(
                keyword,
                status,
                searchType,
                page,
                size
        );

        return ApiResponse.ok("모집글 목록 조회 성공", response);
    }

    // 모집글 상세 조회: 공개 API
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);

        return ApiResponse.ok("모집글 상세 조회 성공", response);
    }

    // 모집글 수정: 작성자 본인만 가능
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);

        PostResponse response = postService.updatePost(email, postId, request);

        return ApiResponse.ok("모집글 수정 성공", response);
    }

    // 모집글 삭제: 작성자 본인만 가능
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String email = getLoginEmail(authentication);

        postService.deletePost(email, postId);

        return ApiResponse.ok("모집글 삭제 성공", null);
    }

    private String getLoginEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        return authentication.getName();
    }
}