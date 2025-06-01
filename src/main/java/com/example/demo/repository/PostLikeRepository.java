package com.example.demo.repository;

import com.example.demo.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);

    // 좋아요한 사용자 목록 조회를 위한 추가 메서드
    List<PostLike> findByPostId(Long postId);
}