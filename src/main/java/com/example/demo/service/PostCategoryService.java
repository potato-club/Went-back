package com.example.demo.service;

import com.example.demo.dto.PostCategoryDTO;
import com.example.demo.entity.PostCategory;
import com.example.demo.repository.PostCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostCategoryService {
    @Autowired
    private PostCategoryRepository postCategoryRepository;

    public PostCategoryDTO createPostCategory(PostCategoryDTO postCategoryDTO) {
        PostCategory postCategory = new PostCategory();
        postCategory.setPostId(postCategoryDTO.getPostId());
        postCategory.setCategoryId(postCategoryDTO.getCategoryId());
        return convertToDTO(postCategoryRepository.save(postCategory));
    }

    public List<PostCategoryDTO> getAllPostCategories() {
        return postCategoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostCategoryDTO getPostCategory(Long id) {
        PostCategory postCategory = postCategoryRepository.findById(id).orElse(null);
        return convertToDTO(postCategory);
    }

    public PostCategoryDTO updatePostCategory(PostCategoryDTO postCategoryDTO) {
        PostCategory postCategory = postCategoryRepository.findById(postCategoryDTO.getPostCategoryId()).orElse(null);
        if (postCategory != null) {
            postCategory.setPostId(postCategoryDTO.getPostId());
            postCategory.setCategoryId(postCategoryDTO.getCategoryId());
            return convertToDTO(postCategoryRepository.save(postCategory));
        }
        return null;
    }

    public void deletePostCategory(Long id) {
        postCategoryRepository.deleteById(id);
    }

    private PostCategoryDTO convertToDTO(PostCategory postCategory) {
        if (postCategory == null) return null;
        PostCategoryDTO dto = new PostCategoryDTO();
        dto.setPostCategoryId(postCategory.getPostCategoryId());
        dto.setPostId(postCategory.getPostId());
        dto.setCategoryId(postCategory.getCategoryId());
        return dto;
    }
}