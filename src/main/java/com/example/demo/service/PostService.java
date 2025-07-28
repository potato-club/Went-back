package com.example.demo.service;

import com.example.demo.dto.response.PostListDTO;
import com.example.demo.dto.request.PostCreationDTO;
import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostPreviewResponseDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.Category;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PostLikeRepository postLikeRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostResponseDTO createPost(PostCreationDTO dto, CustomUserDetails userDetails, List<MultipartFile> files) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("카테고리가 존재하지 않습니다."));
        UserEntity user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(category)
                .user(user)
                .stars(dto.getStars())
                .thumbnailUrl(dto.getThumbnailUrl())
                .build();

        post = postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            s3Service.uploadFiles(files);
        }

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategory().getCategoryId())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .stars(post.getStars())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }

    // 게시글 수정
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostUpdateDTO dto, Long userId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인의 게시글만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("카테고리가 존재하지 않습니다."));

        if (dto.getTitle() != null) post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());
        if (dto.getCategoryId() != null) post.setCategory(category);
        if (dto.getStars() != null) post.setStars(dto.getStars());
        if (dto.getThumbnailUrl() != null) post.setThumbnailUrl(dto.getThumbnailUrl());

        postRepository.save(post);

        if (files != null && !files.isEmpty()) {
            s3Service.uploadFiles(files);
        }

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategory().getCategoryId())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .stars(post.getStars())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostListDTO> getPostsByCategory(Long categoryId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCategory_CategoryId(categoryId, pageable);

        return posts.map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setUserId(String.valueOf(post.getUser().getUserId()));
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCategoryId(post.getCategory().getCategoryId());
            dto.setViewCount(post.getViewCount());
            dto.setStars(post.getStars());
            dto.setThumbnailUrl(post.getThumbnailUrl());
            return dto;
        });
    }

    @Transactional
    public PostResponseDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 존재하지 않습니다."));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        long likeCount = postLikeRepository.countByPost(post);

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryId(post.getCategory().getCategoryId())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .stars(post.getStars())
                .thumbnailUrl(post.getThumbnailUrl())
                .likeCount(likeCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PostPreviewResponseDTO> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findTop4ByUser_UserIdOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(postMapper::toPostPreviewResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostPreviewResponseDTO> getMyLikedPosts(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        Pageable myLikedPostsPageable = PageRequest.of(0, 4);
        List<Post> likedPosts = postLikeRepository.findLikedPostByUser(user, myLikedPostsPageable);

        return likedPosts.stream()
                .map(postMapper::toPostPreviewResponseDTO)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인의 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }
}