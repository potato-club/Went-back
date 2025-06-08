package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;

    @Autowired
    public PostService(PostRepository postRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
    }

    /** 게시글 작성 (별점, 썸네일 포함, 이미지 DB 저장 X) */
    @Transactional
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> files) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setCategoryId(postDTO.getCategoryId());
        post.setStars(postDTO.getStars());
        post.setViewCount(1);
        // 썸네일 DB 미사용이므로 setThumbnailUrl 필요 없음

        post = postRepository.save(post);

        // 반환 DTO
        postDTO.setPostId(post.getPostId());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setViewCount(post.getViewCount());
        postDTO.setStars(post.getStars());
        // 썸네일: 프론트에서 받은 값 그대로 응답
        // (DB에 저장은 안 하지만 응답에 포함)
        // (필요하다면 req에서 받아서 넣은 값 사용)
        return postDTO;
    }

    /** 게시글 상세 조회 (별점, 썸네일 포함) */
    @Transactional
    public PostDTO getPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
        Post post = optionalPost.get();
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setCategoryId(post.getCategoryId());
        dto.setViewCount(post.getViewCount());
        dto.setStars(post.getStars());
        // 썸네일: DB 미사용, 필요 시 프론트가 보내주는 값을 컨트롤러/서비스에서 넘길 수 있음
        // dto.setThumbnailUrl(프론트에서 전달된 값 또는 null);
        return dto;
    }

    /** 게시글 수정 (별점, 썸네일 포함) */
    @Transactional
    public PostDTO updatePost(PostDTO postDTO, List<MultipartFile> files) {
        Optional<Post> optionalPost = postRepository.findById(postDTO.getPostId());
        if (optionalPost.isEmpty()) {
            return null;
        }
        Post post = optionalPost.get();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCategoryId(postDTO.getCategoryId());
        post.setStars(postDTO.getStars());
        // 썸네일 DB 미사용, 별도 처리 안함

        postRepository.save(post);

        PostDTO resultDTO = new PostDTO();
        resultDTO.setPostId(post.getPostId());
        resultDTO.setUserId(post.getUserId());
        resultDTO.setTitle(post.getTitle());
        resultDTO.setContent(post.getContent());
        resultDTO.setCreatedAt(post.getCreatedAt());
        resultDTO.setCategoryId(post.getCategoryId());
        resultDTO.setViewCount(post.getViewCount());
        resultDTO.setStars(post.getStars());
        // resultDTO.setThumbnailUrl(postDTO.getThumbnailUrl());
        return resultDTO;
    }

    @Transactional(readOnly = true)
    public Page<PostListDTO> getPostsByCategory(Long categoryId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByCategoryId(categoryId, pageable);

        return posts.map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setUserId(post.getUserId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCategoryId(post.getCategoryId());
            dto.setViewCount(post.getViewCount());
            dto.setStars(post.getStars());
            // 썸네일: 프론트에서 필요하면 별도 관리
            // dto.setThumbnailUrl(null);
            return dto;
        });
    }

    /** 게시글 삭제 (첨부파일 없음, S3 연동도 필요 없음) */
    @Transactional
    public void deletePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new NoSuchElementException("게시글이 존재하지 않습니다.");
        }
        Post post = optionalPost.get();
        postRepository.delete(post);
    }

    /** 이미지 업로드: S3에만 저장, DB 저장 없음 */
    @Transactional
    public List<String> uploadImages(Long postId, List<MultipartFile> files, String userId) {
        List<String> photoUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = s3Service.upload(file);
            photoUrls.add(url);
        }
        return photoUrls;
    }

    /** 이미지 삭제: S3에서만 삭제, DB에는 영향 없음 */
    @Transactional
    public void deleteImageByUrl(String fileUrl) {
        s3Service.deleteFileByUrl(fileUrl);
    }
}