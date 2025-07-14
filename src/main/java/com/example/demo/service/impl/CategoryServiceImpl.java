package com.example.demo.service.impl;

import com.example.demo.dto.response.CategoryResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(categoryMapper::toCategoryResponseDTO)
                .collect(Collectors.toList());
    }
}
