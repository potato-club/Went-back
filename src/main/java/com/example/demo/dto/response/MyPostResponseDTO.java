package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MyPostResponseDTO {
    private Long postId;
    private String title;
    private int likeCount;
    private int stars;
    private String thumbnailUrl;
    private LocalDateTime createdDate;
}
