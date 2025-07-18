package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategoryPostPreviewResponseDTO {
    private Long categoryId;
    private String categoryName;
    private List<PostPreviewResponseDTO> posts;
}
