package com.devlinker.backend.document.repository;

import com.devlinker.backend.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByTeamIdOrderByIdDesc(Long teamId);
}