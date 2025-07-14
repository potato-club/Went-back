package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryResponseDTO;
import com.example.demo.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO toCategoryResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .categoryType(category.getCategoryType())
                .build();
    }
}