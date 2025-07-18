package com.example.demo.mapper;

import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostResponseDTO toPostResponseDto(Post post) {
        if (post == null) return null;

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .content(post.getContent())
                .categoryId(post.getCategory().getCategoryId())
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

    public PostPreviewResponseDTO toPostPreviewResponseDTO(Post post) {
        return PostPreviewResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdDate(post.getCreatedAt())
                .stars(post.getStars())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .build();
    }
}