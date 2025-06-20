package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "게시글 응답 DTO")
@Getter
@Builder
public class PostResponseDTO {
    @Schema(description = "게시글 ID", example = "100")
    private Long postId;

    @Schema(description = "작성자 ID (User)", example = "1")
    private Long userId;

    @Schema(description = "게시글 본문 내용", example = "오늘의 일상 공유합니다!")
    private String content;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;
}
