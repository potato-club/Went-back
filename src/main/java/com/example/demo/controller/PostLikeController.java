package com.example.demo.controller;

import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Like API", description = "게시글 좋아요 등록, 취소, 상태 및 개수 조회")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(
            summary = "좋아요 누르기",
            description = """
            로그인한 사용자가 해당 게시글에 좋아요를 등록합니다.
            
            - JWT 인증 필요
            - 중복 좋아요는 무시됨
            """
    )
    @PostMapping
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postLikeService.likePost(postId, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "좋아요 취소",
            description = """
            로그인한 사용자가 해당 게시글에 누른 좋아요를 취소합니다.
            
            - JWT 인증 필요
            """
    )
    @DeleteMapping
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        postLikeService.unlikePost(postId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "내가 좋아요 눌렀는지 확인",
            description = """
            로그인한 사용자가 해당 게시글에 좋아요를 눌렀는지 여부를 반환합니다.
            
            - JWT 인증 필요
            - 응답: `true` 또는 `false`
            """
    )
    @GetMapping("/me")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean liked = postLikeService.hasLiked(postId, userDetails.getUserId());
        return ResponseEntity.ok(liked);
    }

    @Operation(
            summary = "게시글 좋아요 수 조회",
            description = """
            해당 게시글이 받은 좋아요 수를 반환합니다.
            
            - 인증 없이도 조회 가능
            - 응답 예시: `42`
            """
    )
    @GetMapping("/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        long count = postLikeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }
}