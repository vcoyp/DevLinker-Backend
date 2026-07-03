package com.devlinker.backend.post.repository;

import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    // 제목 검색
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCaseAndStatus(
            String keyword,
            PostStatus status,
            Pageable pageable
    );

    // 내용 검색
    Page<Post> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Post> findByContentContainingIgnoreCaseAndStatus(
            String keyword,
            PostStatus status,
            Pageable pageable
    );

    // 제목 + 내용 검색
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword,
            String contentKeyword,
            Pageable pageable
    );

    Page<Post> findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCase(
            PostStatus status1,
            String titleKeyword,
            PostStatus status2,
            String contentKeyword,
            Pageable pageable
    );
}