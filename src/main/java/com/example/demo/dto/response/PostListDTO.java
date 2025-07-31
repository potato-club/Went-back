package com.example.demo.dto.response;

import java.time.LocalDateTime;

public class PostListDTO {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long categoryId;
    private Integer viewCount;
    private Integer commentCount;
    private Integer stars; // 별점 필드 추가
    private String thumbnailUrl;
    private Integer likeCount;

    public PostListDTO() {}

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }


    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Integer getStars() { return stars; }        // getter
    public void setStars(Integer stars) { this.stars = stars; } // setter

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}