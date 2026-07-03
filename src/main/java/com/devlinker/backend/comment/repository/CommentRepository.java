package com.devlinker.backend.comment.repository;

import com.devlinker.backend.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByIdAsc(Long postId);

    long countByPostId(Long postId);
}