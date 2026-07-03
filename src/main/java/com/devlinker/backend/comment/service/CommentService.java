package com.devlinker.backend.comment.service;

import com.devlinker.backend.comment.dto.CommentCreateRequest;
import com.devlinker.backend.comment.dto.CommentResponse;
import com.devlinker.backend.comment.entity.Comment;
import com.devlinker.backend.comment.exception.CommentException;
import com.devlinker.backend.comment.repository.CommentRepository;
import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.post.repository.PostRepository;
import com.devlinker.backend.user.entity.User;
import com.devlinker.backend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse createComment(String email, Long postId, CommentCreateRequest request) {
        validateContent(request);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CommentException(
                        HttpStatus.NOT_FOUND,
                        "POST404",
                        "존재하지 않는 모집글입니다."
                ));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommentException(
                        HttpStatus.UNAUTHORIZED,
                        "COMMON401",
                        "인증 정보가 유효하지 않습니다."
                ));

        Comment comment = Comment.create(post, user, request.getContent().trim());
        Comment savedComment = commentRepository.save(comment);

        return CommentResponse.from(savedComment);
    }

    public List<CommentResponse> getComments(Long postId) {
        boolean postExists = postRepository.existsById(postId);

        if (!postExists) {
            throw new CommentException(
                    HttpStatus.NOT_FOUND,
                    "POST404",
                    "존재하지 않는 모집글입니다."
            );
        }

        return commentRepository.findByPostIdOrderByIdAsc(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public void deleteComment(String email, Long commentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CommentException(
                        HttpStatus.UNAUTHORIZED,
                        "COMMON401",
                        "인증 정보가 유효하지 않습니다."
                ));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(
                        HttpStatus.NOT_FOUND,
                        "COMMENT404",
                        "존재하지 않는 댓글입니다."
                ));

        Long loginUserId = user.getId();
        Long commentWriterId = comment.getUser().getId();

        if (!commentWriterId.equals(loginUserId)) {
            throw new CommentException(
                    HttpStatus.FORBIDDEN,
                    "COMMON403",
                    "댓글 작성자만 삭제할 수 있습니다."
            );
        }

        commentRepository.delete(comment);
    }

    private void validateContent(CommentCreateRequest request) {
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CommentException(
                    HttpStatus.BAD_REQUEST,
                    "COMMON400",
                    "댓글 내용을 입력해주세요."
            );
        }
    }
}