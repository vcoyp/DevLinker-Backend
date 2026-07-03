package com.devlinker.backend.schedule.service;

import com.devlinker.backend.schedule.dto.ScheduleCreateRequest;
import com.devlinker.backend.schedule.dto.ScheduleResponse;
import com.devlinker.backend.schedule.entity.Schedule;
import com.devlinker.backend.schedule.repository.ScheduleRepository;
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
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    // 일정 작성
    public ScheduleResponse createSchedule(
            String email,
            Long teamId,
            ScheduleCreateRequest request
    ) {
        User user = findUserByEmail(email);
        Team team = findTeamById(teamId);

        validateTeamMember(teamId, user.getId());
        validateScheduleTime(request);

        Schedule schedule = Schedule.create(
                team,
                user,
                request.getTitle(),
                request.getContent(),
                request.getStartAt(),
                request.getEndAt()
        );

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return ScheduleResponse.from(savedSchedule);
    }

    // 팀 일정 목록 조회
    public List<ScheduleResponse> getSchedules(
            String email,
            Long teamId
    ) {
        User user = findUserByEmail(email);
        findTeamById(teamId);

        validateTeamMember(teamId, user.getId());

        return scheduleRepository.findAllByTeamIdOrderByStartAtAsc(teamId)
                .stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    // 일정 상세 조회
    public ScheduleResponse getSchedule(
            String email,
            Long scheduleId
    ) {
        User user = findUserByEmail(email);
        Schedule schedule = findScheduleById(scheduleId);

        Long teamId = schedule.getTeam().getId();
        validateTeamMember(teamId, user.getId());

        return ScheduleResponse.from(schedule);
    }

    // 일정 삭제
    public void deleteSchedule(
            String email,
            Long scheduleId
    ) {
        User user = findUserByEmail(email);
        Schedule schedule = findScheduleById(scheduleId);

        Long teamId = schedule.getTeam().getId();
        validateTeamMember(teamId, user.getId());

        if (!schedule.getWriter().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "일정 작성자만 삭제할 수 있습니다."
            );
        }

        scheduleRepository.delete(schedule);
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

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "일정을 찾을 수 없습니다."
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

    private void validateScheduleTime(ScheduleCreateRequest request) {
        if (request.getStartAt() == null || request.getEndAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "일정 시작 시간과 종료 시간은 필수입니다."
            );
        }

        if (request.getEndAt().isBefore(request.getStartAt())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "종료 시간은 시작 시간보다 빠를 수 없습니다."
            );
        }
    }
}