package com.example.demo.service;

import com.example.demo.dto.request.PostCreationDTO;
import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Post;
import com.example.demo.entity.UserEntity;
import com.example.demo.error.ErrorCode;
import com.example.demo.error.NotFoundException;
import com.example.demo.mapper.PostMapper;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PostMapper postMapper;

    // 게시글 생성
    @Transactional
    public PostResponseDTO createPost(PostCreationDTO postCreationDTO, List<MultipartFile> files) {
        Category category = categoryRepository.findById(postCreationDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        UserEntity writer = userRepository.findById(postCreationDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("사용자가 존재하지 않습니다.", ErrorCode.USER_NOT_FOUND));

        Post post = postMapper.toPostEntity(postCreationDTO, category, writer);
        Post savedPost = postRepository.save(post);

        savePhotos(post.getPostId(), postCreationDTO.getUserId(), files);

        return postMapper.toPostResponseDto(savedPost);
    }

    // 전체 게시글 조회
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toPostResponseDto)
                .collect(Collectors.toList());
    }

    // 페이지네이션 조회
    public Page<PostResponseDTO> getPagedPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(postMapper::toPostResponseDto);
    }

    // 단건 조회
    public PostResponseDTO getPost(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toPostResponseDto)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다.", ErrorCode.NOT_FOUND));
    }

    // 작성자별 조회
    public List<PostResponseDTO> getPostsByUser(Long userId) {
        return postRepository.findByWriterId(userId).stream()
                .map(postMapper::toPostResponseDto)
                .collect(Collectors.toList());
    }

    // 게시글 수정
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostUpdateDTO postUpdateDTO, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글이 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        Category category = categoryRepository.findById(postUpdateDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다.", ErrorCode.NOT_FOUND));

        postMapper.updatePostEntity(postUpdateDTO, post, category);

        photoRepository.findAllByPostId(post.getPostId()).forEach(photo -> {
            s3Service.deleteFileByUrl(photo.getUrl());
            photoRepository.delete(photo);
        });

        savePhotos(postId, post.getWriter().getUserId(), files);

        return postMapper.toPostResponseDto(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long id) {
        // 사진 먼저 삭제
        List<Photo> photos = photoRepository.findAllByPostId(id);
        for (Photo photo : photos) {
            s3Service.deleteFileByUrl(photo.getUrl());
            photoRepository.delete(photo);
        }

        postRepository.deleteById(id);
    }

    public Page<PostListDTO> getPostsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

        Page<Post> posts = postRepository.findByCategoryId(category.getCategoryId(), pageable);

        return posts.map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setTitle("가공된 제목"); // 또는 post.getTitle() 등 추가
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt().toLocalDate());

            dto.setPhotoUrl(photoRepository.findFirstByPostId(post.getPostId())
                    .map(Photo::getUrl).orElse(null));
            dto.setLikes(postLikeRepository.countByPostId(post.getPostId()));
            dto.setComments(commentRepository.findByPostIdOrderByCreatedAtDesc(post.getPostId()).size());
            dto.setStars(0.0); // 추후 별점 평균 계산 로직으로 대체

            return dto;
        });
    }

    private void savePhotos(Long postId, Long userId, List<MultipartFile> files) {
        // 여러 장 업로드
        if (files == null) return;

        files.forEach(file -> {
            String url = s3Service.uploadFile(userId, postId, file);
            photoRepository.save(
                    Photo.builder()
                            .postId(postId)
                            .url(url)
                            .build()
            );
        });
    }

}
