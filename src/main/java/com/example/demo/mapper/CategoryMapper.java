package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryPostPreviewResponseDTO;
import com.example.demo.dto.response.CategoryResponseDTO;
import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryMapper {
    private final PostMapper postMapper;

    public CategoryResponseDTO toCategoryResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .categoryType(category.getCategoryType())
                .build();
    }

    public CategoryPostPreviewResponseDTO toCategoryPostPreviewResponseDTO(Category category, List<Post> posts) {
        List<PostPreviewResponseDTO> postPreviews = posts.stream()
                .map(postMapper::toPostPreviewResponseDTO)
                .toList();

        return CategoryPostPreviewResponseDTO.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getName())
                .posts(postPreviews)
                .build();
    }
}