package com.example.demo.mapper;

import com.example.demo.dto.response.PostCategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import com.example.demo.entity.PostCategory;
import org.springframework.stereotype.Component;

@Component
public class PostCategoryMapper {
    public PostCategoryDTO toPostCategoryDTO(PostCategory postCategory) {
        if (postCategory == null) return null;

        return PostCategoryDTO.builder()
                .postCategoryId(postCategory.getPostCategoryId())
                .postId(postCategory.getPost().getPostId())
                .categoryId(postCategory.getCategory().getCategoryId())
                .build();
    }

    public PostCategory toPostCategoryEntity(PostCategoryDTO dto, Post post, Category category) {
        if (dto == null) return null;

        return PostCategory.builder()
                .post(post)
                .category(category)
                .build();
    }
}
