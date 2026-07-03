package com.devlinker.backend.team.repository;

import com.devlinker.backend.team.entity.TeamMember;
import com.devlinker.backend.team.entity.TeamMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserIdAndRole(
            Long teamId,
            Long userId,
            TeamMemberRole role
    );

    List<TeamMember> findByUserIdOrderByIdDesc(Long userId);

    List<TeamMember> findByTeamIdOrderByIdAsc(Long teamId);
}