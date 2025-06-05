package com.example.demo.controller;

import com.example.demo.dto.PostDTO;
import com.example.demo.dto.PostListDTO;
import com.example.demo.service.PostService;
import com.example.demo.service.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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

    @Operation(
            summary = "게시글 작성 (이미지 첨부된 파일만 S3에 업로드)",
            description = """
            - post 예시: {"userId": 1, "categoryId": 2, "title": "제목 테스트", "content": "테스트"}
            - files: 여러 장 업로드 가능합니다.
        """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("post") String post,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        PostDTO postDTO = objectMapper.readValue(post, PostDTO.class);
        PostDTO created = postService.createPost(postDTO, files);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "카테고리별 게시글 목록 조회",
            description = """
            📢 **사용 가이드**
            1. 먼저 /categories API를 호출해서 카테고리 목록 및 id를 확인하세요.
            2. 아래 파라미터에 원하는 카테고리 id를 입력해 게시글을 조회할 수 있습니다.

            ▷ **정렬 기준(sort) 옵션**
              - recent   : 최신순 (기본값)
              - likes    : 좋아요순
              - comments : 댓글순
              - stars    : 별점순
              - views    : 조회수순
              - oldest   : 오래된순

            ▷ **예시 요청**
            ```
            /api/posts/list?category=1&sort=likes&page=0&size=8
            ```
            (category는 필수, 나머지는 선택)
        """
    )
    @GetMapping("/list")
    public ResponseEntity<Page<PostListDTO>> getFilteredPosts(
            @Parameter(description = "카테고리 ID (반드시 /categories에서 조회한 id 사용)", example = "1", required = true)
            @RequestParam String category,

            @Parameter(
                    description = """
                    정렬 기준:
                    - recent(최신순, 기본값)
                    - likes(좋아요순)
                    - comments(댓글순)
                    - stars(별점순)
                    - views(조회수순)
                    - oldest(오래된순)
                """,
                    example = "likes"
            )
            @RequestParam(defaultValue = "recent") String sort,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값=0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기 (한 페이지 당 게시글 수, 기본값=8)", example = "8")
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

    /** 요청한 정렬 기준에 맞게 Sort 객체 반환 */
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
            summary = "게시글 단건 조회 (조회 시 조회수 1 증가)",
            description = """
            - path parameter {id}에 게시글의 고유 ID를 입력하세요.
            - 해당 게시글을 조회할 때마다 조회수가 1씩 증가합니다.
            - 예: /api/posts/10
            - 존재하지 않는 id로 조회 시 404 Not Found 반환
        """
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(
            @Parameter(description = "게시글 고유 ID", example = "10", required = true)
            @PathVariable Long id) {
        PostDTO postDTO = postService.getPost(id);
        return postDTO != null ? ResponseEntity.ok(postDTO) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "게시글 수정 (파일 포함)",
            description = """
            - 게시글 고유 id({id})에 해당하는 게시글을 수정합니다.
            - 본문 및 제목 등 post 정보와, 파일(이미지 등)을 함께 수정할 수 있습니다.
            - 파일을 새로 첨부하면 기존 파일이 교체되거나 추가됩니다.
            - 첨부 파일 없이도 수정 가능 (files 파트는 생략 가능)
            - 요청 예시:
              - post: JSON 문자열(예: {"title":"수정 제목",  "categoryId": 수정 아이디, "content":"수정 본문",...})
              - files: 이미지 파일(선택)
            - 존재하지 않는 게시글 id로 수정 시 404 Not Found 반환
        """
    )
    @PostMapping(value = "/{id}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestPart("post") String post,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        PostDTO postDTO = objectMapper.readValue(post, PostDTO.class);
        postDTO.setPostId(id);
        PostDTO updatedPost = postService.updatePost(postDTO, files);
        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "게시글 삭제",
            description = """
            - 게시글 고유 id({id})에 해당하는 게시글을 삭제합니다.
            - 존재하지 않는 게시글 id로 요청할 경우 404 Not Found 반환
            - 삭제 성공 시 204 No Content 응답(본문 없음)
            - 연관된 첨부파일 등도 함께 삭제 처리됨
            - 예시 요청: /api/posts/10 (DELETE)
        """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글의 고유 ID", example = "10", required = true)
            @PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}