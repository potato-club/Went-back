package com.example.demo.service;

import com.example.demo.dto.response.CategoryPostPreviewResponseDTO;

public interface MainService {
    CategoryPostPreviewResponseDTO getCategoryPostPreview(Long categoryId);
}
