package com.devlinker.backend.post.service;

import com.devlinker.backend.global.exception.NotFoundException;
import com.devlinker.backend.post.dto.PostCreateRequest;
import com.devlinker.backend.post.dto.PostResponse;
import com.devlinker.backend.post.dto.PostSearchType;
import com.devlinker.backend.post.dto.PostUpdateRequest;
import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.entity.PostStatus;
import com.devlinker.backend.post.repository.PostRepository;
import com.devlinker.backend.team.repository.TeamRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository,
            TeamRepository teamRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    // 모집글 작성
    public PostResponse createPost(String email, PostCreateRequest request) {
        User user = findUserByEmail(email);

        Post post = Post.create(
                user,
                request.getTitle(),
                request.getContent(),
                request.getRecruitCount()
        );

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    // 모집글 목록 조회 + 검색/필터 + 페이징
    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(
            String keyword,
            PostStatus status,
            PostSearchType searchType,
            int page,
            int size
    ) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasStatus = status != null;

        if (searchType == null) {
            searchType = PostSearchType.TITLE_CONTENT;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<Post> posts;

        if (!hasKeyword && !hasStatus) {
            posts = postRepository.findAll(pageable);
        } else if (!hasKeyword) {
            posts = postRepository.findByStatus(status, pageable);
        } else if (!hasStatus) {
            posts = searchWithoutStatus(keyword, searchType, pageable);
        } else {
            posts = searchWithStatus(keyword, status, searchType, pageable);
        }

        return posts.map(PostResponse::from);
    }

    private Page<Post> searchWithoutStatus(
            String keyword,
            PostSearchType searchType,
            Pageable pageable
    ) {
        if (searchType == PostSearchType.TITLE) {
            return postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        }

        if (searchType == PostSearchType.CONTENT) {
            return postRepository.findByContentContainingIgnoreCase(keyword, pageable);
        }

        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword,
                keyword,
                pageable
        );
    }

    private Page<Post> searchWithStatus(
            String keyword,
            PostStatus status,
            PostSearchType searchType,
            Pageable pageable
    ) {
        if (searchType == PostSearchType.TITLE) {
            return postRepository.findByTitleContainingIgnoreCaseAndStatus(
                    keyword,
                    status,
                    pageable
            );
        }

        if (searchType == PostSearchType.CONTENT) {
            return postRepository.findByContentContainingIgnoreCaseAndStatus(
                    keyword,
                    status,
                    pageable
            );
        }

        return postRepository.findByStatusAndTitleContainingIgnoreCaseOrStatusAndContentContainingIgnoreCase(
                status,
                keyword,
                status,
                keyword,
                pageable
        );
    }

    // 모집글 상세 조회 + 조회수 증가
    public PostResponse getPost(Long postId) {
        Post post = findPostById(postId);

        post.increaseViewCount();

        return PostResponse.from(post);
    }

    // 모집글 수정
    public PostResponse updatePost(String email, Long postId, PostUpdateRequest request) {
        User user = findUserByEmail(email);
        Post post = findPostById(postId);

        validateWriter(user, post);

        PostStatus status = request.getStatus();

        if (status == null) {
            status = post.getStatus();
        }

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getRecruitCount(),
                status
        );

        return PostResponse.from(post);
    }

    // 모집글 삭제
    public void deletePost(String email, Long postId) {
        User user = findUserByEmail(email);
        Post post = findPostById(postId);

        validateWriter(user, post);
        validatePostCanBeDeleted(post);

        postRepository.delete(post);
    }

    private void validatePostCanBeDeleted(Post post) {
        boolean hasTeam = teamRepository.findByPostId(post.getId()).isPresent();

        if (hasTeam) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "팀이 생성된 모집글은 삭제할 수 없습니다."
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

    private void validateWriter(User user, Post post) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("작성자만 수정 또는 삭제할 수 있습니다.");
        }
    }
}