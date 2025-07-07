package com.example.demo.controller;

import com.example.demo.dto.request.CommentRequestDTO;
import com.example.demo.dto.request.CommentUpdateDTO;
import com.example.demo.dto.response.CommentResponseDTO;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment API", description = "댓글 등록, 조회, 수정, 삭제, 페이징 및 대댓글 정렬 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 등록",
            description = """
            로그인한 사용자가 특정 게시글에 댓글 또는 대댓글을 작성합니다.

            JWT 인증 필요  
            ✏ Request Body 예시:
            ```json
            {
              "postId": 1,
              "content": "댓글 내용입니다",
              "parentId": null
            }
            ```
            - `parentId`가 `null`이면 일반 댓글  
            - 특정 댓글 ID를 넣으면 해당 댓글에 대한 대댓글로 저장됩니다.
            """
    )
    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommentResponseDTO comment = commentService.add(
                request.getPostId(),
                userDetails.getUserId(),
                request.getContent(),
                request.getParentId()
        );
        return ResponseEntity.ok(comment);
    }

    @Operation(
            summary = "댓글 전체 조회 (최신순)",
            description = """
            - 특정 게시글(postId)의 모든 댓글을 최신순으로 가져옵니다.
            - 대댓글 관계와 정렬은 고려되지 않습니다.
            - 인증 없이도 접근 가능합니다.
            """
    )
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long postId) {
        List<CommentResponseDTO> list = commentService.getAll(postId);
        return ResponseEntity.ok(list);
    }

    @Operation(
            summary = "댓글 삭제",
            description = """
            로그인한 사용자가 **자신이 작성한 댓글**을 삭제합니다.

            - JWT 인증 필요  
            - 본인이 작성한 댓글이 아니면 삭제 불가 (403 에러 발생)
            """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.delete(id, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "댓글 수정",
            description = """
            로그인한 사용자가 자신이 작성한 댓글의 내용을 수정합니다.

            - JWT 인증 필요  
            - Request Body 예시:
            ```json
            {
              "content": "수정된 댓글 내용"
            }
            ```
            """
    )
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateDTO updateDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommentResponseDTO updated = commentService.update(id, userDetails.getUserId(), updateDTO.getContent());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "댓글 페이징 조회",
            description = """
            - 특정 게시글에 달린 댓글들을 페이징 처리하여 가져옵니다.
            - 정렬 기준: 작성일(createdAt) 내림차순
            - 인증 없이도 접근 가능

            Request Params:
            - `page`: 페이지 번호 (0부터 시작)
            - `size`: 한 페이지에 보여줄 댓글 수
            """
    )
    @GetMapping("/{postId}/paged")
    public ResponseEntity<Page<CommentResponseDTO>> getPagedComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponseDTO> result = commentService.getPaged(postId, page, size);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "댓글 대댓글 구조 정렬 조회",
            description = """
            댓글을 **부모-대댓글 구조로 정렬**해서 가져옵니다.

            - 정렬 기준: `parentId ASC`, `createdAt ASC`
            - 인증 없이도 접근 가능
            """
    )
    @GetMapping("/{postId}/structured")
    public ResponseEntity<List<CommentResponseDTO>> getStructuredComments(@PathVariable Long postId) {
        List<CommentResponseDTO> list = commentService.getStructuredComments(postId);
        return ResponseEntity.ok(list);
    }

}