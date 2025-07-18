package com.example.demo.service;

import com.example.demo.dto.response.CategoryResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    public List<CategoryResponseDTO> getAllCategories();
}
