package com.example.demo.controller;

import com.example.demo.dto.request.PostCreationDTO;
import com.example.demo.dto.request.PostUpdateDTO;
import com.example.demo.dto.response.PostResponseDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Post API", description = "게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시글 작성 (파일 업로드 포함)")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestPart("post") PostCreationDTO postCreationDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) {
        PostResponseDTO newPost = postService.createPost(postCreationDTO, multipartFiles);
        return ResponseEntity.ok(newPost);
    }

    @Operation(summary = "전체 게시글 조회")
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @Operation(summary = "게시글 페이지네이션 조회")
    @GetMapping("/page")
    public ResponseEntity<Page<PostResponseDTO>> getPagedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getPagedPosts(pageable));
    }

    @Operation(summary = "카테고리/정렬/페이지 기반 게시글 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<Page<PostListDTO>> getFilteredPosts(
            @RequestParam String category,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size, getSortOption(sort));
        Page<PostListDTO> result = postService.getPostsByCategory(category, pageable);
        return ResponseEntity.ok(result);
    }

    private Sort getSortOption(String sort) {
        return switch (sort) {
            case "likes" -> Sort.by(Sort.Direction.DESC, "likes");
            case "comments" -> Sort.by(Sort.Direction.DESC, "commentCount");
            case "stars" -> Sort.by(Sort.Direction.DESC, "stars");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long id) {
        PostResponseDTO postResponseDTO = postService.getPost(id);
        return postResponseDTO != null ? ResponseEntity.ok(postResponseDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "게시글 수정 (파일 포함)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long id,
            @RequestPart("post") PostUpdateDTO postUpdateDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) {
        PostResponseDTO updatedPost = postService.updatePost(id, postUpdateDTO, multipartFiles);
        return ResponseEntity.ok(updatedPost);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}