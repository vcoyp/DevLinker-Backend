package com.devlinker.backend.application.service;

import com.devlinker.backend.application.dto.ApplicationDecisionResponse;
import com.devlinker.backend.application.entity.Application;
import com.devlinker.backend.application.entity.ApplicationStatus;
import com.devlinker.backend.application.exception.ApplicationException;
import com.devlinker.backend.application.repository.ApplicationRepository;
import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.repository.PostRepository;
import com.devlinker.backend.team.entity.Team;
import com.devlinker.backend.team.entity.TeamMember;
import com.devlinker.backend.team.entity.TeamMemberRole;
import com.devlinker.backend.team.repository.TeamMemberRepository;
import com.devlinker.backend.team.repository.TeamRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.entity.UserRole;
import com.devlinker.backend.user.entity.UserStatus;
import com.devlinker.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ApplicationService(
                applicationRepository,
                postRepository,
                userRepository,
                teamRepository,
                teamMemberRepository
        );
    }

    @Test
    void approveApplication_success() {
        // given
        User writer = user(1L, "writer@test.com", "writer");
        User applicant = user(2L, "applicant@test.com", "applicant");
        Post post = post(10L, writer);
        Application application = application(100L, post, applicant);

        when(userRepository.findByEmail("writer@test.com"))
                .thenReturn(Optional.of(writer));
        when(applicationRepository.findById(100L))
                .thenReturn(Optional.of(application));
        when(teamRepository.findByPostId(10L))
                .thenReturn(Optional.empty());

        when(teamRepository.save(any(Team.class)))
                .thenAnswer(invocation -> {
                    Team savedTeam = invocation.getArgument(0);
                    setId(savedTeam, 200L);
                    return savedTeam;
                });

        when(teamMemberRepository.existsByTeamIdAndUserId(200L, 1L))
                .thenReturn(false);
        when(teamMemberRepository.existsByTeamIdAndUserId(200L, 2L))
                .thenReturn(false);

        // when
        ApplicationDecisionResponse response =
                applicationService.approveApplication("writer@test.com", 100L);

        // then
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(response.getApplicationId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(response.getTeamId()).isEqualTo(200L);
        assertThat(response.getTeamName()).isEqualTo("테스트 모집글 팀");

        ArgumentCaptor<TeamMember> captor = ArgumentCaptor.forClass(TeamMember.class);
        verify(teamMemberRepository, times(2)).save(captor.capture());

        assertThat(captor.getAllValues())
                .extracting(TeamMember::getRole)
                .containsExactlyInAnyOrder(TeamMemberRole.OWNER, TeamMemberRole.MEMBER);
    }

    @Test
    void approveApplication_fail_whenNotPostWriter() {
        // given
        User writer = user(1L, "writer@test.com", "writer");
        User otherUser = user(3L, "other@test.com", "other");
        User applicant = user(2L, "applicant@test.com", "applicant");
        Post post = post(10L, writer);
        Application application = application(100L, post, applicant);

        when(userRepository.findByEmail("other@test.com"))
                .thenReturn(Optional.of(otherUser));
        when(applicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        // when & then
        assertThatThrownBy(() ->
                applicationService.approveApplication("other@test.com", 100L)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage("모집글 작성자만 수행할 수 있습니다.")
                .satisfies(exception -> {
                    ApplicationException ex = (ApplicationException) exception;
                    assertThat(ex.getCode()).isEqualTo("APPLICATION403");
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                });

        verify(teamRepository, never()).save(any());
        verify(teamMemberRepository, never()).save(any());
    }

    @Test
    void approveApplication_fail_whenAlreadyProcessed() {
        // given
        User writer = user(1L, "writer@test.com", "writer");
        User applicant = user(2L, "applicant@test.com", "applicant");
        Post post = post(10L, writer);
        Application application = application(100L, post, applicant);
        application.approve();

        when(userRepository.findByEmail("writer@test.com"))
                .thenReturn(Optional.of(writer));
        when(applicationRepository.findById(100L))
                .thenReturn(Optional.of(application));

        // when & then
        assertThatThrownBy(() ->
                applicationService.approveApplication("writer@test.com", 100L)
        )
                .isInstanceOf(ApplicationException.class)
                .hasMessage("이미 처리된 지원입니다.")
                .satisfies(exception -> {
                    ApplicationException ex = (ApplicationException) exception;
                    assertThat(ex.getCode()).isEqualTo("APPLICATION400");
                    assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });

        verify(teamRepository, never()).save(any());
        verify(teamMemberRepository, never()).save(any());
    }

    private User user(Long id, String email, String nickname) {
        User user = new User(
                email,
                "password1234",
                nickname,
                UserRole.USER,
                UserStatus.ACTIVE
        );
        setId(user, id);
        return user;
    }

    private Post post(Long id, User writer) {
        Post post = Post.create(
                writer,
                "테스트 모집글",
                "테스트 내용",
                3
        );
        setId(post, id);
        return post;
    }

    private Application application(Long id, Post post, User applicant) {
        Application application = Application.create(post, applicant);
        setId(application, id);
        return application;
    }

    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}