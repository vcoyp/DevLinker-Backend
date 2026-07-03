package com.devlinker.backend.retrospective.service;

import com.devlinker.backend.retrospective.dto.RetrospectiveCreateRequest;
import com.devlinker.backend.retrospective.dto.RetrospectiveResponse;
import com.devlinker.backend.retrospective.entity.Retrospective;
import com.devlinker.backend.retrospective.repository.RetrospectiveRepository;
import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.repository.TeamMemberRepository;
import com.devlinker.backend.team.repository.TeamRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public RetrospectiveResponse createRetrospective(
            Long teamId,
            Long userId,
            RetrospectiveCreateRequest request
    ) {
        validateTeamMember(teamId, userId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 팀입니다."
                ));

        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 사용자입니다."
                ));

        Retrospective retrospective = Retrospective.create(
                team,
                writer,
                request.getTitle(),
                request.getGoodPoint(),
                request.getProblemPoint(),
                request.getImprovementPoint()
        );

        Retrospective savedRetrospective = retrospectiveRepository.save(retrospective);

        return RetrospectiveResponse.from(savedRetrospective);
    }

    public List<RetrospectiveResponse> getRetrospectives(Long teamId, Long userId) {
        validateTeamMember(teamId, userId);

        return retrospectiveRepository.findByTeamIdOrderByIdDesc(teamId)
                .stream()
                .map(RetrospectiveResponse::from)
                .toList();
    }

    public RetrospectiveResponse getRetrospective(Long retrospectiveId, Long userId) {
        Retrospective retrospective = findRetrospective(retrospectiveId);

        Long teamId = retrospective.getTeam().getId();
        validateTeamMember(teamId, userId);

        return RetrospectiveResponse.from(retrospective);
    }

    @Transactional
    public void deleteRetrospective(Long retrospectiveId, Long userId) {
        Retrospective retrospective = findRetrospective(retrospectiveId);

        Long teamId = retrospective.getTeam().getId();
        validateTeamMember(teamId, userId);

        if (!retrospective.getWriter().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "작성자만 회고를 삭제할 수 있습니다."
            );
        }

        retrospectiveRepository.delete(retrospective);
    }

    private Retrospective findRetrospective(Long retrospectiveId) {
        return retrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 회고입니다."
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
}