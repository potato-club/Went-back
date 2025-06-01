package com.example.demo.controller;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.service.PostService;
import com.example.demo.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "Post API", description = "게시글 및 이미지 API")
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

    @Operation(summary = "게시글 작성 (이미지 첨부된 파일만 S3에 업로드)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("post") PostDTO postDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        PostDTO created = postService.createPost(postDTO, files);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "카테고리별 게시글 목록 조회",
            description = """
                        1. /categories API를 통해 카테고리 목록을 조회하세요.
                        2. 원하는 카테고리의 id를 category 파라미터로 입력해 게시글을 조회합니다.
                    
                        정렬 기준(sort) 옵션:
                        - recent: 최신순 (기본값)
                        - likes: 좋아요순
                        - comments: 댓글순
                        - stars: 별점순
                        - views: 조회수순
                        - oldest: 오래된순
                    """
    )
    @GetMapping("/list")
    public ResponseEntity<Page<PostListDTO>> getFilteredPosts(
            @Parameter(description = "카테고리 ID", example = "1", required = true)
            @RequestParam String category,

            @Parameter(
                    description = "정렬 기준 (recent: 최신순, likes: 좋아요순, " +
                            "comments: 댓글순, " +
                            "stars: 별점순, views: 조회수순, oldest: 오래된순)",
                    example = "likes"
            )
            @RequestParam(defaultValue = "recent") String sort,

            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기(한 페이지 당 게시글 수)", example = "8")
            @RequestParam(defaultValue = "8") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, getSortOption(sort));
        try {
            Long categoryId = Long.valueOf(category);
            Page<PostListDTO> result = postService.getPostsByCategory(categoryId, pageable);
            return ResponseEntity.ok(result);
        } catch (NumberFormatException e) {
            // 카테고리 값이 올바르지 않은 경우 400 에러
            return ResponseEntity.badRequest().body(Page.empty());
        }
    }

    // 정렬 기준에 따라 Sort 객체를 반환
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

    @Operation(summary = "게시글 단건 조회 (조회 시 조회수 증가)")
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        // 서비스 계층에서 조회수 1 증가시키고 결과 반환
        PostDTO postDTO = postService.getPost(id);
        return postDTO != null ? ResponseEntity.ok(postDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "게시글 수정 (파일 포함)")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestPart("post") PostDTO postDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        postDTO.setPostId(id);
        PostDTO updatedPost = postService.updatePost(postDTO, files);
        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}