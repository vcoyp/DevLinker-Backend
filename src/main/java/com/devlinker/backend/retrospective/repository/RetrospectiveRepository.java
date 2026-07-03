package com.devlinker.backend.retrospective.repository;

import com.devlinker.backend.retrospective.entity.Retrospective;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {

    List<Retrospective> findByTeamIdOrderByIdDesc(Long teamId);
}