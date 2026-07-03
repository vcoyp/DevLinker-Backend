package com.devlinker.backend.application.repository;

import com.devlinker.backend.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByPostIdAndApplicantId(Long postId, Long applicantId);

    Optional<Application> findByPostIdAndApplicantId(Long postId, Long applicantId);

    List<Application> findByPostIdOrderByIdAsc(Long postId);
}