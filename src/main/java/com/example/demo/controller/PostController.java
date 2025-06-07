package com.example.demo.controller;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.service.PostService;
import com.example.demo.service.S3Service;
import com.example.demo.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "Post API", description = "게시글 CRUD 및 이미지 파일 업로드/삭제 (S3만 사용, DB 저장 없음)")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

    @Autowired
    public PostController(PostService postService, S3Service s3Service) {
        this.postService = postService;
        this.s3Service = s3Service;
    }

    // -------- 게시글 CRUD --------

    @Operation(
            summary = "게시글 작성",
            description = """
            - 제목/내용/카테고리 입력해서 게시글 등록
            - Swagger: Try it out → JSON 입력 → Execute
            - 예시: { "title": "제목", "content": "내용", "categoryId": 1 }
        """
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestBody PostDTO postDTO,
            @RequestAttribute(value = "user", required = false) UserResponseDTO user
    ) {
        if (user == null) {
            user = UserResponseDTO.builder().socialKey("test-key").build();
        }
        postDTO.setUserId(user.getSocialKey());
        PostDTO created = postService.createPost(postDTO, null);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "게시글 수정",
            description = """
            - 게시글 정보(제목/내용/카테고리)만 수정
            - Swagger: Try it out → id 입력 → JSON 입력 → Execute
        """
    )
    @PostMapping(value = "/{id}/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostDTO postDTO,
            @RequestAttribute(value = "user", required = false) UserResponseDTO user
    ) {
        if (user == null) {
            user = UserResponseDTO.builder().socialKey("test-key").build();
        }
        postDTO.setPostId(id);
        postDTO.setUserId(user.getSocialKey());
        PostDTO updatedPost = postService.updatePost(postDTO, null);
        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "카테고리별 게시글 목록 조회",
            description = """
            - 원하는 카테고리/정렬/페이지로 게시글 조회
            - Swagger: Try it out → 파라미터 입력 → Execute
        """
    )
    @GetMapping("/list")
    public ResponseEntity<Page<PostListDTO>> getFilteredPosts(
            @RequestParam String category,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, getSortOption(sort));
        try {
            Long categoryId = Long.valueOf(category);
            Page<PostListDTO> result = postService.getPostsByCategory(categoryId, pageable);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Page.empty());
        }
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

    @Operation(
            summary = "게시글 상세 조회",
            description = """
            - 게시글 번호로 단일 게시글 조회
            - Swagger: Try it out → id 입력 → Execute
        """
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        PostDTO postDTO = postService.getPost(id);
        return postDTO != null ? ResponseEntity.ok(postDTO) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "게시글 삭제",
            description = """
            - 게시글 번호로 삭제
            - Swagger: Try it out → id 입력 → Execute
        """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -------- 이미지 파일 업로드(S3만) --------

    @Operation(
            summary = "이미지 파일 업로드",
            description = """
            - 게시글과 무관하게 이미지(파일)만 업로드, DB 저장 없이 S3에만 저장
            - Swagger: Try it out → files에서 여러 장 선택 → Execute
            - 응답: 업로드된 이미지의 URL 리스트 반환
        """
    )
    @PostMapping(value = "/images/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadImagesOnly(
            @RequestParam("files") List<MultipartFile> files,
            @RequestAttribute(value = "user", required = false) UserResponseDTO user
    ) {
        if (user == null) {
            user = UserResponseDTO.builder().socialKey("test-key").build();
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = s3Service.upload(file);
            urls.add(url); // Photo DB에 저장하지 않음!
        }
        return ResponseEntity.ok(urls);
    }

    // -------- 이미지 파일 삭제(S3만) --------

    @Operation(
            summary = "이미지 파일 삭제 (경로로)",
            description = """
            - 업로드한 이미지의 URL(전체 경로)을 그대로 넘기면 삭제 (DB 작업 없음)
            - Swagger: Try it out → url에 삭제할 파일 전체경로 입력 → Execute
            - 성공 시 204 No Content
        """
    )
    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImageByUrl(
            @RequestParam("url") String fileUrl,
            @RequestAttribute(value = "user", required = false) UserResponseDTO user
    ) {
        if (user == null) {
            user = UserResponseDTO.builder().socialKey("test-key").build();
        }
        s3Service.deleteFileByUrl(fileUrl); // DB 작업 없음!
        return ResponseEntity.noContent().build();
    }
}