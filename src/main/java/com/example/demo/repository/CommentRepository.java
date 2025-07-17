package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글 ID로 댓글 리스트 (최신순)
    List<Comment> findByPost_PostIdOrderByCreatedAtDesc(Long postId);

    // 게시글 ID로 댓글 수 카운트
    int countByPost_PostId(Long postId);

    // 특정 댓글이 특정 유저의 것인지 확인
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.user.userId = :userId")
    Optional<Comment> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    // 유저가 작성한 모든 댓글
    List<Comment> findByUser_UserId(Long userId);

    // 페이징 처리
    Page<Comment> findByPost_PostId(Long postId, Pageable pageable);

    // 계층적 댓글 구조
    List<Comment> findByPost_PostIdOrderByParentIdAscCreatedAtAsc(Long postId);
}