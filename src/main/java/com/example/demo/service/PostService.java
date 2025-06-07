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
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final S3Service s3Service;

    @Autowired
    public PostService(PostRepository postRepository, S3Service s3Service) {
        this.postRepository = postRepository;
        this.s3Service = s3Service;
    }

    /** 게시글 작성 (이미지 없음, JSON만) */
    @Transactional
    public PostDTO createPost(PostDTO postDTO, List<MultipartFile> files) {
        Post post = new Post();
        post.setUserId(postDTO.getUserId()); // String userId
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setCategoryId(postDTO.getCategoryId());
        post.setViewCount(1);
        post = postRepository.save(post);

        postDTO.setPostId(post.getPostId());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setPhotoUrls(Collections.emptyList()); // 파일정보는 관리하지 않음
        postDTO.setViewCount(post.getViewCount());
        return postDTO;
    }

    /** 게시글 상세 조회 (조회수 증가, 첨부파일 없음) */
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
        dto.setPhotoUrls(Collections.emptyList()); // 첨부파일 관리 X

        return dto;
    }

    /** 게시글 수정 (이미지 없음, 텍스트만) */
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
        postRepository.save(post);

        PostDTO resultDTO = new PostDTO();
        resultDTO.setPostId(post.getPostId());
        resultDTO.setUserId(post.getUserId());
        resultDTO.setTitle(post.getTitle());
        resultDTO.setContent(post.getContent());
        resultDTO.setCreatedAt(post.getCreatedAt());
        resultDTO.setCategoryId(post.getCategoryId());
        resultDTO.setPhotoUrls(Collections.emptyList());
        resultDTO.setViewCount(post.getViewCount());

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
            dto.setPhotoUrls(Collections.emptyList());
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
            photoUrls.add(url); // DB 저장 없이 URL만 반환
        }
        return photoUrls;
    }

    /** 이미지 삭제: S3에서만 삭제, DB에는 영향 없음 */
    @Transactional
    public void deleteImageByUrl(String fileUrl) {
        s3Service.deleteFileByUrl(fileUrl);
    }
}