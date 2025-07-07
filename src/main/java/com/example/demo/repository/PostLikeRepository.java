package com.example.demo.repository;

import com.example.demo.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 해당 게시글에 해당 유저가 이미 좋아요 했는지 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 해당 게시글에 대해 유저의 좋아요 삭제 (좋아요 취소)
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // 게시글에 대한 전체 좋아요 수 조회
    long countByPostId(Long postId);

    // 게시글에 좋아요한 사용자 전체 목록 (통계 또는 관리용)
    List<PostLike> findByPostId(Long postId);

    // 특정 유저가 누른 좋아요 목록 (유저 입장에서 내가 누른 글들 보기 등)
    List<PostLike> findByUserId(Long userId);

    // 좋아요 단건 객체 조회 (있으면 삭제용 등으로 활용 가능)
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
}
