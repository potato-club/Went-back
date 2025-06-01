package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment API", description = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(
            @RequestParam Long postId,
            @RequestParam Long userId,
            @RequestBody String content
    ) {
        Comment comment = commentService.add(postId, userId, content);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> list = commentService.getAll(postId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}