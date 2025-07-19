package com.example.demo.service;

import com.example.demo.dto.response.PostCategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostCategory;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.mapper.PostCategoryMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostCategoryRepository;
import com.example.demo.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCategoryService {
    private final PostCategoryRepository postCategoryRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostCategoryMapper postCategoryMapper;

    @Transactional
    public PostCategoryDTO createPostCategory(PostCategoryDTO postCategoryDTO) {
        Post post = postRepository.findById(postCategoryDTO.getPostId())
                .orElseThrow(() -> new NotFoundException("게시물이 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        Category category = categoryRepository.findById(postCategoryDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        PostCategory postCategory = postCategoryMapper.toPostCategoryEntity(postCategoryDTO, post, category);
        PostCategory savedPostCategory = postCategoryRepository.save(postCategory);

        return postCategoryMapper.toPostCategoryDTO(savedPostCategory);
    }

    public List<PostCategoryDTO> getAllPostCategories() {
        return postCategoryRepository.findAll().stream()
                .map(postCategoryMapper::toPostCategoryDTO)
                .collect(Collectors.toList());
    }

    public PostCategoryDTO getPostCategory(Long id) {
        return postCategoryRepository.findById(id)
                .map(postCategoryMapper::toPostCategoryDTO)
                .orElseThrow(() -> new NotFoundException("게시글 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));
    }

    @Transactional
    public PostCategoryDTO updatePostCategory(PostCategoryDTO postCategoryDTO) {
        PostCategory postCategory = postCategoryRepository.findById(postCategoryDTO.getPostCategoryId())
                .orElseThrow(() -> new NotFoundException("게시글 카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        Category category = categoryRepository.findById(postCategoryDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        postCategory.changeCategory(category);

        return postCategoryMapper.toPostCategoryDTO(postCategory);
    }

    @Transactional
    public void deletePostCategory(Long id) {
        postCategoryRepository.deleteById(id);
    }
}


