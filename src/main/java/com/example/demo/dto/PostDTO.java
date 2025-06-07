package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    private Long postId;
    private String userId; // String (socialKey)
    private String content;
    private LocalDateTime createdAt;
    private Long categoryId;
    private List<String> photoUrls;
    private String title;
    private int viewCount;

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

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
}