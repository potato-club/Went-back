package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;

    public Comment add(Long postId, Long userId, String content) {
        return commentRepo.save(new Comment(null, postId, userId, content, null));
    }

    public List<Comment> getAll(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public void delete(Long commentId) {
        commentRepo.deleteById(commentId);
    }
}