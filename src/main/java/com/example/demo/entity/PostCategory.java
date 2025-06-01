package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_category")
public class PostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postCategoryId;

    private Long postId;
    private Long categoryId;

    // Getters and Setters
    public Long getPostCategoryId() { return postCategoryId; }
    public void setPostCategoryId(Long postCategoryId) { this.postCategoryId = postCategoryId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}