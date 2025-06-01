package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글과 카테고리 간 연결 정보를 담는 DTO")
public class PostCategoryDTO {

    @Schema(description = "게시글-카테고리 연결 ID", example = "1001")
    private Long postCategoryId;

    @Schema(description = "연결된 게시글 ID", example = "20")
    private Long postId;

    @Schema(description = "연결된 카테고리 ID", example = "3")
    private Long categoryId;

    public Long getPostCategoryId() {
        return postCategoryId;
    }

    public void setPostCategoryId(Long postCategoryId) {
        this.postCategoryId = postCategoryId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}