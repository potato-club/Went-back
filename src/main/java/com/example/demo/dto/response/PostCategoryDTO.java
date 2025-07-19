package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "게시글과 카테고리 간 연결 정보를 담는 DTO")
public class PostCategoryDTO {

    @Schema(description = "게시글-카테고리 연결 ID", example = "1001")
    private Long postCategoryId;

    @Schema(description = "연결된 게시글 ID", example = "20")
    private Long postId;

    @Schema(description = "연결된 카테고리 ID", example = "3")
    private Long categoryId;
}