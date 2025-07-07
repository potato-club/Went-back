package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "게시글 응답 DTO")
@Getter
@Builder
public class PostResponseDTO {
    @Schema(description = "게시글 ID", example = "100")
    private Long postId;

    @Schema(description = "작성자 ID (User)", example = "1")
    private Long userId;

    @Schema(description = "게시글 제목", example = "오늘의 일상")
    private String title;

    @Schema(description = "게시글 본문 내용", example = "오늘의 일상 공유합니다!")
    private String content;

    @Schema(description = "카테고리 ID", example = "3")
    private Long categoryId;

    @Schema(description = "생성 시간", example = "2025-06-21T16:33:00")
    private LocalDateTime createdAt;

    @Schema(description = "조회수", example = "10")
    private Integer viewCount;

    @Schema(description = "별점", example = "4")
    private Integer stars;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "좋아요", example = "22")
    private Long likeCount;
}