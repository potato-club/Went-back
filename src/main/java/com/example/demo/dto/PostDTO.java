package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 정보를 담는 DTO")
public class PostDTO {

    @Schema(description = "게시글 ID", example = "100")
    private Long postId;

    @Schema(description = "작성자 ID (User)", example = "1")
    private Long userId;

    @Schema(description = "게시글 본문 내용", example = "오늘의 일상 공유합니다!")
    private String content;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
