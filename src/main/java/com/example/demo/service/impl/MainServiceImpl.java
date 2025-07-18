package com.example.demo.service.impl;

import com.example.demo.dto.response.CategoryPostPreviewResponseDTO;
import com.example.demo.dto.response.MainContentResponseDTO;
import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.dto.response.UserHomeResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.mapper.PostMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public MainContentResponseDTO getMainContent(Long userId) {
        UserHomeResponseDTO user = getUserHomeResponseDTO(userId);
        List<CategoryPostPreviewResponseDTO> posts = getCategoryPostPreviews();

        return MainContentResponseDTO.builder()
                .user(user)
                .posts(posts)
                .build();
    }


    private UserHomeResponseDTO getUserHomeResponseDTO(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserHomeResponseDTO(user);
    }

    private List<CategoryPostPreviewResponseDTO> getCategoryPostPreviews() {
        return categoryRepository.findAll().stream()
                .map(category -> {
                    List<Post> posts = postRepository
                            .findTop3ByCategory_CategoryIdOrderByCreatedAtDesc(category.getCategoryId());

                    return categoryMapper.toCategoryPostPreviewResponseDTO(category, posts);
                })
                .toList();
    }
}
