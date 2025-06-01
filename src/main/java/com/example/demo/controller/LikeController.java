package com.example.demo.controller;

import com.example.demo.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/{userId}")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long postId,
            @PathVariable Long userId) {
        boolean result = likeService.toggleLike(postId, userId);
        return ResponseEntity.ok(result ? "Liked" : "Unliked");
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        long count = likeService.countLikes(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{postId}/users")
    public ResponseEntity<List<Long>> getUserIdsWhoLiked(@PathVariable Long postId) {
        List<Long> userIds = likeService.getUserIdsWhoLikedPost(postId);
        return ResponseEntity.ok(userIds);
    }
}