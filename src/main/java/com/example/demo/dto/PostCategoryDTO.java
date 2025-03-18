package com.example.demo.dto;

public class PostCategoryDTO {
    private Long postCategoryId;
    private Long postId;
    private Long categoryId;

    public Long getPostCategoryId() { return postCategoryId; }
    public void setPostCategoryId(Long postCategoryId) { this.postCategoryId = postCategoryId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}

