package com.example.demo.controller;

import com.example.demo.dto.PostListDTO;
import com.example.demo.dto.request.PostCreationDTO;
import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PostService;
import com.example.demo.service.PostLikeService;
import com.example.demo.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "Post API", description = "게시글 CRUD, 이미지 S3 업로드/삭제 (DB 저장 없음)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;
    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 작성 (JSON)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestBody PostCreationDTO req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostResponseDTO created = postService.createPost(req, userDetails.getUserId(), null);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "게시글 수정 (JSON)")
    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateDTO req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostResponseDTO updated = postService.updatePost(postId, req, userDetails.getUserId(), null);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "카테고리별 게시글 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<Page<PostListDTO>> getFilteredPosts(
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, getSortOption(sort));
        Page<PostListDTO> result = postService.getPostsByCategory(categoryId, pageable);
        return ResponseEntity.ok(result);
    }

    private Sort getSortOption(String sort) {
        return switch (sort) {
            case "likes" -> Sort.by(Sort.Direction.DESC, "likes");
            case "comments" -> Sort.by(Sort.Direction.DESC, "commentCount");
            case "stars" -> Sort.by(Sort.Direction.DESC, "stars");
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long postId) {
        PostResponseDTO postDTO = postService.getPost(postId);
        return ResponseEntity.ok(postDTO);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postService.deletePost(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이미지 파일 업로드 (S3만)")
    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadImagesOnly(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> urls = s3Service.uploadFiles(files);
        return ResponseEntity.ok(urls);
    }

    @Operation(summary = "이미지 파일 삭제 (경로로)")
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImageByUrl(@RequestParam("url") String fileUrl) {
        s3Service.deleteFileByUrl(fileUrl);
        return ResponseEntity.noContent().build();
    }
}