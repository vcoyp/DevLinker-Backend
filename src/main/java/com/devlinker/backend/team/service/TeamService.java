package com.devlinker.backend.team.service;

import com.devlinker.backend.global.exception.NotFoundException;
import com.devlinker.backend.notice.repository.NoticeRepository;
import com.devlinker.backend.team.dto.TeamDashboardMemberResponse;
import com.devlinker.backend.team.dto.TeamDashboardNoticeResponse;
import com.devlinker.backend.team.dto.TeamDashboardResponse;
import com.devlinker.backend.team.dto.TeamDetailResponse;
import com.devlinker.backend.team.dto.TeamMemberResponse;
import com.devlinker.backend.team.dto.TeamResponse;
import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.entity.TeamMember;
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
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository,
            NoticeRepository noticeRepository
    ) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.noticeRepository = noticeRepository;
    }

    public List<TeamResponse> getMyTeams(String email) {
        User user = findUserByEmail(email);

        return teamMemberRepository.findByUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(TeamResponse::from)
                .toList();
    }

    public TeamDetailResponse getTeamDetail(String email, Long teamId) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamMember(team.getId(), user.getId());

        return TeamDetailResponse.from(team);
    }

    public List<TeamMemberResponse> getTeamMembers(String email, Long teamId) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamMember(team.getId(), user.getId());

        return teamMemberRepository.findByTeamIdOrderByIdAsc(team.getId())
                .stream()
                .map(TeamMemberResponse::from)
                .toList();
    }

    public TeamDashboardResponse getTeamDashboard(String email, Long teamId) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        TeamMember loginMember = findTeamMember(team.getId(), user.getId());

        List<TeamDashboardMemberResponse> members =
                teamMemberRepository.findByTeamIdOrderByIdAsc(team.getId())
                        .stream()
                        .map(TeamDashboardMemberResponse::from)
                        .toList();

        List<TeamDashboardNoticeResponse> recentNotices =
                noticeRepository.findByTeamIdOrderByIdDesc(team.getId())
                        .stream()
                        .limit(5)
                        .map(TeamDashboardNoticeResponse::from)
                        .toList();

        return TeamDashboardResponse.of(
                team,
                loginMember.getRole(),
                members,
                recentNotices
        );
    }

    private void validateTeamMember(Long teamId, Long userId) {
        boolean isTeamMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);

        if (!isTeamMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "팀원만 접근할 수 있습니다."
            );
        }
    }

    private TeamMember findTeamMember(Long teamId, Long userId) {
        return teamMemberRepository.findByTeamIdOrderByIdAsc(teamId)
                .stream()
                .filter(teamMember -> teamMember.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "팀원만 접근할 수 있습니다."
                ));
    }

    private Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException("TEAM404", "존재하지 않는 팀입니다."));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("USER404", "존재하지 않는 사용자입니다."));
    }
}