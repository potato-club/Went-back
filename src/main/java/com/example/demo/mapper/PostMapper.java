package com.example.demo.mapper;

import com.example.demo.dto.request.PostCreationDTO;
import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import com.example.demo.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostResponseDTO toPostResponseDto(Post post) {
        if (post == null) return null;

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getWriter().getUserId())
                .content(post.getContent())
                .categoryId(post.getCategory().getCategoryId())
                .build();
    }

    public Post toPostEntity(PostCreationDTO dto, Category category, UserEntity writer) {
        if (dto == null) return null;

        return Post.builder()
                .content(dto.getContent())
                .category(category)
                .writer(writer)
                .build();
    }

    public void updatePostEntity(PostUpdateDTO dto, Post post, Category category) {
        if (dto.getContent() != null) {
            post.updateContent(dto.getContent());
        }

        if (dto.getCategoryId() != null) {
            post.updateCategory(category);
        }
    }
}