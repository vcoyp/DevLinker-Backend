package com.devlinker.backend.document.service;

import com.devlinker.backend.document.dto.DocumentCreateRequest;
import com.devlinker.backend.document.dto.DocumentResponse;
import com.devlinker.backend.document.entity.Document;
import com.devlinker.backend.document.repository.DocumentRepository;
import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.repository.TeamMemberRepository;
import com.devlinker.backend.team.repository.TeamRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    // 문서 작성
    public DocumentResponse createDocument(
            String email,
            Long teamId,
            DocumentCreateRequest request
    ) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamMember(teamId, user.getId());
        validateDocumentRequest(request);

        Document document = Document.create(
                team,
                user,
                request.getTitle(),
                request.getContent()
        );

        Document savedDocument = documentRepository.save(document);

        return DocumentResponse.from(savedDocument);
    }

    // 팀 문서 목록 조회
    public List<DocumentResponse> getDocuments(
            String email,
            Long teamId
    ) {
        User user = findUserByEmail(email);
        findTeamById(teamId);

        validateTeamMember(teamId, user.getId());

        return documentRepository.findAllByTeamIdOrderByIdDesc(teamId)
                .stream()
                .map(DocumentResponse::from)
                .toList();
    }

    // 문서 상세 조회
    public DocumentResponse getDocument(
            String email,
            Long documentId
    ) {
        User user = findUserByEmail(email);
        Document document = findDocumentById(documentId);

        Long teamId = document.getTeam().getId();
        validateTeamMember(teamId, user.getId());

        return DocumentResponse.from(document);
    }

    // 문서 삭제
    public void deleteDocument(
            String email,
            Long documentId
    ) {
        User user = findUserByEmail(email);
        Document document = findDocumentById(documentId);

        Long teamId = document.getTeam().getId();
        validateTeamMember(teamId, user.getId());

        if (!document.getWriter().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "문서 작성자만 삭제할 수 있습니다."
            );
        }

        documentRepository.delete(document);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "팀을 찾을 수 없습니다."
                ));
    }

    private Document findDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "문서를 찾을 수 없습니다."
                ));
    }

    private void validateTeamMember(Long teamId, Long userId) {
        boolean isTeamMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);

        if (!isTeamMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "해당 팀의 멤버만 접근할 수 있습니다."
            );
        }
    }

    private void validateDocumentRequest(DocumentCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "문서 제목은 필수입니다."
            );
        }

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "문서 내용은 필수입니다."
            );
        }
    }
}