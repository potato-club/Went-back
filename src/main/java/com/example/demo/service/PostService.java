package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Photo;
import com.example.demo.entity.Post;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private CategoryRepository categoryRepository;


    // 게시글 생성
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> multipartFiles) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId());
        post.setContent(postDTO.getContent());
        post.setCategoryId(postDTO.getCategoryId());
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        // 여러 장 업로드
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile file : multipartFiles) {
                s3Service.uploadFile(savedPost.getUserId(), savedPost.getPostId(), file);
            }
        }

        return convertToDTO(savedPost);
    }

    // 전체 게시글 조회
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 페이지네이션 조회
    public Page<PostDTO> getPagedPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    // 단건 조회
    public PostDTO getPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        return convertToDTO(post);
    }

    // 게시글 수정
    public PostDTO updatePost(PostDTO postDTO, List<MultipartFile> multipartFiles) {
        Post post = postRepository.findById(postDTO.getPostId()).orElse(null);
        if (post != null) {
            post.setUserId(postDTO.getUserId());
            post.setContent(postDTO.getContent());
            post.setCategoryId(postDTO.getCategoryId());

            // 기존 사진 모두 삭제
            List<Photo> existingPhotos = photoRepository.findAllByPostId(post.getPostId());
            for (Photo photo : existingPhotos) {
                s3Service.deleteFileByUrl(photo.getUrl());
                photoRepository.delete(photo);
            }

            // 새로 업로드
            if (multipartFiles != null && !multipartFiles.isEmpty()) {
                for (MultipartFile file : multipartFiles) {
                    s3Service.uploadFile(post.getUserId(), post.getPostId(), file);
                }
            }

            return convertToDTO(postRepository.save(post));
        }
        return null;
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        // 사진 먼저 삭제
        List<Photo> photos = photoRepository.findAllByPostId(id);
        for (Photo photo : photos) {
            s3Service.deleteFileByUrl(photo.getUrl());
            photoRepository.delete(photo);
        }

        postRepository.deleteById(id);
    }

    // Entity -> DTO 변환
    private PostDTO convertToDTO(Post post) {
        if (post == null) return null;
        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setCategoryId(post.getCategoryId());
        return dto;
    }

    public Page<PostListDTO> getPostsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

        Page<Post> posts = postRepository.findByCategoryId(category.getId(), pageable);

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

}
