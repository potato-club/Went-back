package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글별 댓글 리스트 (최신순)
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 게시글별 댓글 수 카운트
    int countByPostId(Long postId);

    // 특정 댓글이 특정 유저의 것인지 확인 (삭제 권한 검증용 등)
    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    // 유저가 작성한 모든 댓글 (마이페이지 등 확장용)
    List<Comment> findByUserId(Long userId);

    Page<Comment> findByPostId(Long postId, Pageable pageable);

    List<Comment> findByPostIdOrderByParentIdAscCreatedAtAsc(Long postId);

}