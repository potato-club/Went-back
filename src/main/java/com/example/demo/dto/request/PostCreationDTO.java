package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "게시글 생성 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreationDTO {
    @Schema(description = "작성자 ID (User)", example = "1")
    private Long userId;

    @Schema(description = "게시글 제목", example = "오늘의 일상")
    private String title;

    @Schema(description = "게시글 본문 내용", example = "오늘의 일상 공유합니다!")
    private String content;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    @Schema(description = "별점", example = "4")
    private Integer stars;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;
}