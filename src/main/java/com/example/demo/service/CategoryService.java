package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setKoName(categoryDTO.getKoName());
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        return convertToDTO(category);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryDTO.getCategoryId()).orElse(null);
        if (category != null) {
            category.setName(categoryDTO.getName());
            category.setKoName(categoryDTO.getKoName());
            categoryRepository.save(category);
            return convertToDTO(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        if (category == null) return null;
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getId());
        dto.setName(category.getName());
        dto.setKoName(category.getKoName());
        return dto;
    }
}