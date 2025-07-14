package com.example.demo.dto.response;

import com.example.demo.entity.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private CategoryType categoryType;
}
