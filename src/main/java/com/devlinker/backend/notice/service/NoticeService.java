package com.devlinker.backend.notice.service;

import com.devlinker.backend.global.exception.NotFoundException;
import com.devlinker.backend.notice.dto.NoticeCreateRequest;
import com.devlinker.backend.notice.dto.NoticeResponse;
import com.devlinker.backend.notice.dto.NoticeUpdateRequest;
import com.devlinker.backend.notice.entity.Notice;
import com.devlinker.backend.notice.repository.NoticeRepository;
import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.entity.TeamMemberRole;
import com.devlinker.backend.team.repository.TeamMemberRepository;
import com.devlinker.backend.team.repository.TeamRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public NoticeService(
            NoticeRepository noticeRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        this.noticeRepository = noticeRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    public NoticeResponse createNotice(
            String email,
            Long teamId,
            NoticeCreateRequest request
    ) {
        User writer = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamOwner(team.getId(), writer.getId());
        validateNoticeRequest(request.getTitle(), request.getContent());

        Notice notice = Notice.create(
                team,
                writer,
                request.getTitle().trim(),
                request.getContent().trim()
        );

        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResponse.from(savedNotice);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(String email, Long teamId) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamMember(team.getId(), user.getId());

        return noticeRepository.findByTeamIdOrderByIdDesc(team.getId())
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NoticeResponse getNoticeDetail(String email, Long noticeId) {
        User user = findUserByEmail(email);
        Notice notice = findNoticeById(noticeId);

        validateTeamMember(notice.getTeam().getId(), user.getId());

        return NoticeResponse.from(notice);
    }

    public NoticeResponse updateNotice(
            String email,
            Long noticeId,
            NoticeUpdateRequest request
    ) {
        User user = findUserByEmail(email);
        Notice notice = findNoticeById(noticeId);

        validateTeamOwner(notice.getTeam().getId(), user.getId());
        validateNoticeRequest(request.getTitle(), request.getContent());

        notice.update(
                request.getTitle().trim(),
                request.getContent().trim()
        );

        return NoticeResponse.from(notice);
    }

    public void deleteNotice(String email, Long noticeId) {
        User user = findUserByEmail(email);
        Notice notice = findNoticeById(noticeId);

        validateTeamOwner(notice.getTeam().getId(), user.getId());

        noticeRepository.delete(notice);
    }

    private void validateTeamMember(Long teamId, Long userId) {
        boolean isTeamMember = teamMemberRepository.existsByTeamIdAndUserId(
                teamId,
                userId
        );

        if (!isTeamMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "팀원만 접근할 수 있습니다."
            );
        }
    }

    private void validateTeamOwner(Long teamId, Long userId) {
        boolean isOwner = teamMemberRepository.existsByTeamIdAndUserIdAndRole(
                teamId,
                userId,
                TeamMemberRole.OWNER
        );

        if (!isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "팀 OWNER만 수행할 수 있습니다."
            );
        }
    }

    private void validateNoticeRequest(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("공지 제목은 필수입니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("공지 내용은 필수입니다.");
        }
    }

    private Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException(
                        "NOTICE404",
                        "존재하지 않는 공지입니다."
                ));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(
                        "TEAM404",
                        "존재하지 않는 팀입니다."
                ));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        "USER404",
                        "존재하지 않는 사용자입니다."
                ));
    }
}