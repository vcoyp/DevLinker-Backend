package com.devlinker.backend.application.service;

import com.devlinker.backend.application.dto.ApplicationDecisionResponse;
import com.devlinker.backend.application.dto.ApplicationMyStatusResponse;
import com.devlinker.backend.application.dto.ApplicationResponse;
import com.devlinker.backend.application.entity.Application;
import com.devlinker.backend.application.entity.ApplicationStatus;
import com.devlinker.backend.application.exception.ApplicationException;
import com.devlinker.backend.application.repository.ApplicationRepository;
import com.devlinker.backend.global.exception.NotFoundException;
import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.entity.PostStatus;
import com.devlinker.backend.post.repository.PostRepository;
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

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    // 지원하기
    public ApplicationResponse createApplication(String email, Long postId) {
        User applicant = findUserByEmail(email);
        Post post = findPostById(postId);

        validateCanApply(applicant, post);

        Application application = Application.create(post, applicant);
        Application savedApplication = applicationRepository.save(application);

        return ApplicationResponse.from(savedApplication);
    }

    // 내 지원 상태 조회
    @Transactional(readOnly = true)
    public ApplicationMyStatusResponse getMyApplicationStatus(String email, Long postId) {
        User loginUser = findUserByEmail(email);
        Post post = findPostById(postId);

        return applicationRepository.findByPostIdAndApplicantId(
                        post.getId(),
                        loginUser.getId()
                )
                .map(ApplicationMyStatusResponse::from)
                .orElseGet(ApplicationMyStatusResponse::notApplied);
    }

    // 지원자 목록 조회: 모집글 작성자만 가능
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByPost(String email, Long postId) {
        User loginUser = findUserByEmail(email);
        Post post = findPostById(postId);

        validatePostWriter(loginUser, post);

        return applicationRepository.findByPostIdOrderByIdAsc(postId)
                .stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    // 지원 취소: 지원자 본인만 가능
    public void cancelApplication(String email, Long applicationId) {
        User loginUser = findUserByEmail(email);
        Application application = findApplicationById(applicationId);

        if (!application.getApplicant().getId().equals(loginUser.getId())) {
            throw new ApplicationException(
                    "APPLICATION403",
                    "지원자 본인만 지원을 취소할 수 있습니다.",
                    HttpStatus.FORBIDDEN
            );
        }

        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new ApplicationException(
                    "APPLICATION400",
                    "이미 처리된 지원은 취소할 수 없습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }

        application.cancel();
    }

    // 승인: 모집글 작성자만 가능, 승인 시 팀 생성 및 팀원 추가
    public ApplicationDecisionResponse approveApplication(String email, Long applicationId) {
        User loginUser = findUserByEmail(email);
        Application application = findApplicationById(applicationId);
        Post post = application.getPost();

        validatePostWriter(loginUser, post);
        validateApplicationIsApplied(application);

        Team team = teamRepository.findByPostId(post.getId())
                .orElseGet(() -> {
                    Team newTeam = Team.create(post);
                    return teamRepository.save(newTeam);
                });

        addTeamMemberIfNotExists(team, post.getUser(), TeamMemberRole.OWNER);
        addTeamMemberIfNotExists(team, application.getApplicant(), TeamMemberRole.MEMBER);

        application.approve();

        return ApplicationDecisionResponse.approve(
                application.getId(),
                application.getStatus(),
                team.getId(),
                team.getName()
        );
    }

    // 거절: 모집글 작성자만 가능
    public ApplicationDecisionResponse rejectApplication(String email, Long applicationId) {
        User loginUser = findUserByEmail(email);
        Application application = findApplicationById(applicationId);
        Post post = application.getPost();

        validatePostWriter(loginUser, post);
        validateApplicationIsApplied(application);

        application.reject();

        return ApplicationDecisionResponse.reject(
                application.getId(),
                application.getStatus()
        );
    }

    private void addTeamMemberIfNotExists(Team team, User user, TeamMemberRole role) {
        boolean alreadyExists = teamMemberRepository.existsByTeamIdAndUserId(
                team.getId(),
                user.getId()
        );

        if (!alreadyExists) {
            TeamMember teamMember = TeamMember.create(team, user, role);
            teamMemberRepository.save(teamMember);
        }
    }

    private void validateCanApply(User applicant, Post post) {
        if (post.getUser().getId().equals(applicant.getId())) {
            throw new ApplicationException(
                    "APPLICATION400",
                    "본인 모집글에는 지원할 수 없습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (post.getStatus() == PostStatus.CLOSED) {
            throw new ApplicationException(
                    "APPLICATION400",
                    "모집 완료된 글에는 지원할 수 없습니다.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (applicationRepository.existsByPostIdAndApplicantId(post.getId(), applicant.getId())) {
            throw new ApplicationException(
                    "APPLICATION400",
                    "이미 지원한 모집글입니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validatePostWriter(User user, Post post) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ApplicationException(
                    "APPLICATION403",
                    "모집글 작성자만 수행할 수 있습니다.",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private void validateApplicationIsApplied(Application application) {
        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new ApplicationException(
                    "APPLICATION400",
                    "이미 처리된 지원입니다.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("USER404", "존재하지 않는 사용자입니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST404", "존재하지 않는 모집글입니다."));
    }

    private Application findApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationException(
                        "APPLICATION404",
                        "존재하지 않는 지원입니다.",
                        HttpStatus.NOT_FOUND
                ));
    }
}