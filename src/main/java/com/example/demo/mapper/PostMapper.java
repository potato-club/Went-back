package com.example.demo.mapper;

import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostPreviewResponseDTO toPostPreviewResponseDTO(Post post) {
        return PostPreviewResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .createdDate(post.getCreatedAt())
                .stars(post.getStars())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .viewCount(post.getViewCount() != null ? post.getViewCount() : 0)
                .build();
    }
}