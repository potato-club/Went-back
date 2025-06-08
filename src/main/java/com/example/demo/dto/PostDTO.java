package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long postId;
    private String userId; // String (socialKey)
    private String content;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String title;
    private int viewCount;
    private Integer stars;
    private String thumbnailUrl;// 별점 필드 추가

    public PostDTO() {}

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public Integer getStars() { return stars; }            // Getter
    public void setStars(Integer stars) { this.stars = stars; } // Setter

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}