package com.example.demo.service;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.Photo;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.PhotoRepository;
import com.example.demo.service.S3Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;
    private final S3Service s3Service;

    @Autowired
    public PostService(PostRepository postRepository,
                       PhotoRepository photoRepository,
                       S3Service s3Service) {
        this.postRepository = postRepository;
        this.photoRepository = photoRepository;
        this.s3Service = s3Service;
    }

    // 게시글 작성 + 이미지 업로드
    @Transactional
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> files) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setCategoryId(postDTO.getCategoryId());

        post = postRepository.save(post);

        List<String> photoUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                // 파일 업로드 후 URL 반환
                String url = s3Service.upload(file);

                // Photo 엔티티 생성 및 저장
                Photo photo = new Photo();
                photo.setPostId(post.getPostId());
                photo.setUserId(post.getUserId());
                photo.setUrl(url);
                photoRepository.save(photo);

                photoUrls.add(url);
            }
        }

        postDTO.setPostId(post.getPostId());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setPhotoUrls(photoUrls);

        return postDTO;
    }

    // 게시글 단건 조회 (사진 포함)
    @Transactional(readOnly = true)
    public PostDTO getPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
        Post post = optionalPost.get();

        List<Photo> photos = photoRepository.findAllByPostId(postId);
        List<String> photoUrls = photos.stream()
                .map(Photo::getUrl)
                .collect(Collectors.toList());

        PostDTO dto = new PostDTO();
        dto.setPostId(post.getPostId());
        dto.setUserId(post.getUserId());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setCategoryId(post.getCategoryId());
        dto.setPhotoUrls(photoUrls);

        return dto;
    }

    // 게시글 수정 (사진 모두 삭제 후 재업로드)
    @Transactional
    public PostDTO updatePost(PostDTO postDTO, List<MultipartFile> files) {
        Optional<Post> optionalPost = postRepository.findById(postDTO.getPostId());
        if (optionalPost.isEmpty()) {
            return null;
        }
        Post post = optionalPost.get();

        post.setContent(postDTO.getContent());
        post.setCategoryId(postDTO.getCategoryId());
        postRepository.save(post);

        // 기존 사진 삭제(S3 포함)
        List<Photo> existingPhotos = photoRepository.findAllByPostId(post.getPostId());
        for (Photo photo : existingPhotos) {
            s3Service.deleteFileByUrl(photo.getUrl());
        }
        photoRepository.deleteAll(existingPhotos);

        List<String> photoUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                String url = s3Service.upload(file);

                Photo photo = new Photo();
                photo.setPostId(post.getPostId());
                photo.setUserId(post.getUserId());
                photo.setUrl(url);
                photoRepository.save(photo);

                photoUrls.add(url);
            }
        }

        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setPhotoUrls(photoUrls);

        return postDTO;
    }

    // 게시글 삭제 (사진도 모두 삭제)
    @Transactional
    public void deletePost(Long postId) {
        List<Photo> photos = photoRepository.findAllByPostId(postId);
        for (Photo photo : photos) {
            s3Service.deleteFileByUrl(photo.getUrl());
        }
        photoRepository.deleteAll(photos);
        postRepository.deleteById(postId);
    }

    // 카테고리별 페이징 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostListDTO> getPostsByCategory(String category, Pageable pageable) {
        Long categoryId;
        try {
            categoryId = Long.parseLong(category);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Category는 숫자여야 합니다.");
        }

        Page<Post> posts = postRepository.findAllByCategoryId(categoryId, pageable);

        return posts.map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setUserId(post.getUserId());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCategoryId(post.getCategoryId());

            List<Photo> photoList = photoRepository.findAllByPostId(post.getPostId());
            dto.setPhotoUrls(photoList.stream()
                    .map(Photo::getUrl)
                    .collect(Collectors.toList()));

            return dto;
        });
    }
}
