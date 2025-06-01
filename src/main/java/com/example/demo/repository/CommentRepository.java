package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글별 댓글 리스트 (최신순)
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 게시글별 댓글 수 카운트
    int countByPostId(Long postId);
}