package com.devlinker.backend.application.entity;

import com.devlinker.backend.post.entity.Post;
import com.devlinker.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 모집글에 대한 지원인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 누가 지원했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Application() {
    }

    private Application(Post post, User applicant) {
        this.post = post;
        this.applicant = applicant;
        this.status = ApplicationStatus.APPLIED;
    }

    public static Application create(Post post, User applicant) {
        return new Application(post, applicant);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ApplicationStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }

    public void approve() {
        this.status = ApplicationStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ApplicationStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public User getApplicant() {
        return applicant;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}