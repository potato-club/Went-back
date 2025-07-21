package com.example.demo.service.impl;

import com.example.demo.dto.response.CategoryPostPreviewResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public CategoryPostPreviewResponseDTO getCategoryPostPreview(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다.", ErrorCode.CATEGORY_NOT_FOUND));

        List<Post> posts = postRepository.findTop6ByCategory_CategoryIdOrderByCreatedAtDesc(categoryId);

        return categoryMapper.toCategoryPostPreviewResponseDTO(category, posts);
    }
}
