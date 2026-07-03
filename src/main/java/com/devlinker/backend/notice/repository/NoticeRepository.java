package com.devlinker.backend.notice.repository;

import com.devlinker.backend.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByTeamIdOrderByIdDesc(Long teamId);
}