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

    /**
     * 게시글 작성 (이미지 S3 업로드)
     */
    @Transactional
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> files) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setCategoryId(postDTO.getCategoryId());
        post.setViewCount(0);
        post.setViewCount(post.getViewCount() + 1);

        post = postRepository.save(post);

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

        postDTO.setPostId(post.getPostId());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setPhotoUrls(photoUrls);
        postDTO.setViewCount(post.getViewCount());

        return postDTO;
    }

    /**
     * 게시글 상세 조회 (사진 포함, 조회수 증가)
     */
    @Transactional
    public PostDTO getPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return null;
        }
        Post post = optionalPost.get();

        // 조회수 1 증가
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

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
        dto.setViewCount(post.getViewCount());
        dto.setPhotoUrls(photoUrls);

        return dto;
    }

    /**
     * 게시글 수정 (기존 사진 전체 삭제 후 새로 등록)
     */
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

        // 새 파일 업로드 및 저장
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

        PostDTO resultDTO = new PostDTO();
        resultDTO.setPostId(post.getPostId());
        resultDTO.setUserId(post.getUserId());
        resultDTO.setContent(post.getContent());
        resultDTO.setCreatedAt(post.getCreatedAt());
        resultDTO.setCategoryId(post.getCategoryId());
        resultDTO.setPhotoUrls(photoUrls);
        resultDTO.setViewCount(post.getViewCount());

        return resultDTO;
    }

    @Transactional(readOnly = true)
    public Page<PostListDTO> getPostsByCategory(Long categoryId, Pageable pageable) {
        // 1. 게시글 조회 (페이징)
        Page<Post> posts = postRepository.findAllByCategoryId(categoryId, pageable);

        // 2. 게시글 ID 리스트 추출
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getPostId)
                .collect(Collectors.toList());

        // 3. 사진 한 번에 조회 (postId별로 그룹핑)
        final Map<Long, List<String>> photoMap;
        if (!postIds.isEmpty()) {
            List<Photo> photos = photoRepository.findAllByPostIdIn(postIds);
            photoMap = photos.stream().collect(Collectors.groupingBy(
                    Photo::getPostId,
                    Collectors.mapping(Photo::getUrl, Collectors.toList())
            ));
        } else {
            photoMap = Collections.emptyMap();
        }

        // 4. DTO로 변환(필요한 모든 정보 세팅)
        return posts.map(post -> {
            PostListDTO dto = new PostListDTO();
            dto.setPostId(post.getPostId());
            dto.setUserId(post.getUserId());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setCategoryId(post.getCategoryId());
            dto.setViewCount(post.getViewCount());
            // photoMap은 반드시 미리 만들어두어야 하며, 없다면 빈 리스트 반환
            dto.setPhotoUrls(photoMap.getOrDefault(post.getPostId(), Collections.emptyList()));

            // 필요하다면 댓글 수, 좋아요 수 등 추가 필드 세팅
            // dto.setCommentCount(...);
            // dto.setLikeCount(...);

            return dto;
        });
    }

    /**
     * 게시글 삭제 (사진 및 S3 삭제 포함)
     */
    @Transactional
    public void deletePost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new NoSuchElementException("게시글이 존재하지 않습니다.");
        }
        Post post = optionalPost.get();

        List<Photo> photos = photoRepository.findAllByPostId(post.getPostId());
        for (Photo photo : photos) {
            s3Service.deleteFileByUrl(photo.getUrl());
        }
        photoRepository.deleteAll(photos);

        postRepository.delete(post);
    }
}