package com.example.demo.controller;

import com.example.demo.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post API", description = "게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        boolean liked = likeService.toggleLike(postId, userId);
        return ResponseEntity.ok(liked);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.countLikes(postId));
    }
}
