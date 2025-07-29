package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // setter 추가
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "stars", nullable = false)
    private Integer stars = 0;

    private String thumbnailUrl; // 추가

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    // 좋아요 연관관계
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    // 댓글 연관관계 추가
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, Category category, UserEntity user, Integer stars, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
        this.stars = stars;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}