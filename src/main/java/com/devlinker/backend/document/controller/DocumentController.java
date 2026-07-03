package com.devlinker.backend.document.controller;

import com.devlinker.backend.document.dto.DocumentCreateRequest;
import com.devlinker.backend.document.dto.DocumentResponse;
import com.devlinker.backend.document.service.DocumentService;
import com.devlinker.backend.global.response.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // 문서 작성
    @PostMapping("/api/teams/{teamId}/documents")
    public ApiResponse<DocumentResponse> createDocument(
            Authentication authentication,
            @PathVariable Long teamId,
            @RequestBody DocumentCreateRequest request
    ) {
        DocumentResponse response = documentService.createDocument(
                authentication.getName(),
                teamId,
                request
        );

        return ApiResponse.ok("문서 작성 성공", response);
    }

    // 팀 문서 목록 조회
    @GetMapping("/api/teams/{teamId}/documents")
    public ApiResponse<List<DocumentResponse>> getDocuments(
            Authentication authentication,
            @PathVariable Long teamId
    ) {
        List<DocumentResponse> response = documentService.getDocuments(
                authentication.getName(),
                teamId
        );

        return ApiResponse.ok("문서 목록 조회 성공", response);
    }

    // 문서 상세 조회
    @GetMapping("/api/documents/{documentId}")
    public ApiResponse<DocumentResponse> getDocument(
            Authentication authentication,
            @PathVariable Long documentId
    ) {
        DocumentResponse response = documentService.getDocument(
                authentication.getName(),
                documentId
        );

        return ApiResponse.ok("문서 상세 조회 성공", response);
    }

    // 문서 삭제
    @DeleteMapping("/api/documents/{documentId}")
    public ApiResponse<Void> deleteDocument(
            Authentication authentication,
            @PathVariable Long documentId
    ) {
        documentService.deleteDocument(
                authentication.getName(),
                documentId
        );

        return ApiResponse.ok("문서 삭제 성공", null);
    }
}