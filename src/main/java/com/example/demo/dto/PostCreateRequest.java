package com.example.demo.dto;

public class PostCreateRequest {
    private String title;
    private String content;
    private Long categoryId;
    private Integer stars;   // 별점 필드 추가
    private String thumbnailUrl;

    public PostCreateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Integer getStars() { return stars; }           // Getter
    public void setStars(Integer stars) { this.stars = stars; } // Setter

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}