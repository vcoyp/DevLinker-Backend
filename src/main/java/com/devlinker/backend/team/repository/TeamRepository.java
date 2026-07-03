package com.devlinker.backend.team.repository;

import com.devlinker.backend.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByPostId(Long postId);
}