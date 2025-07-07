package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter // setter 추가
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title; // 추가
    @Lob
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity writer;

    private Integer stars; // 추가
    private String thumbnailUrl; // 추가
    private Integer viewCount; // 추가

    @Builder
    public Post(String title, String content, Category category, UserEntity writer, Integer stars, String thumbnailUrl) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.writer = writer;
        this.stars = stars;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 1; // 초기값 설정
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}