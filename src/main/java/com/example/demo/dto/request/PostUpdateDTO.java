package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "게시글 수정 DTO")
@Getter
@NoArgsConstructor
public class PostUpdateDTO {
    @Schema(description = "수정 제목", example = "변경된 제목")
    private String title;

    @Schema(description = "수정 본문 내용", example = "변경된 내용입니다!")
    private String content;

    @Schema(description = "수정 카테고리 ID", example = "4")
    private Long categoryId;

    @Schema(description = "수정 별점", example = "5")
    private Integer stars;

    @Schema(description = "수정 썸네일 URL", example = "https://example.com/new-thumbnail.jpg")
    private String thumbnailUrl;
}