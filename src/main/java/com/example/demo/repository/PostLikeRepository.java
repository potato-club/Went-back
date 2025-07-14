package com.example.demo.repository;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 해당 게시글에 해당 유저가 이미 좋아요 했는지 확인
    boolean existsByPostAndUser(Post post, UserEntity user);

    // 해당 게시글에 대해 유저의 좋아요 삭제
    void deleteByPostAndUser(Post post, UserEntity user);

    // 게시글에 대한 전체 좋아요 수 조회
    long countByPost(Post post);

    // 게시글에 좋아요한 사용자 전체 목록
    List<PostLike> findByPost(Post post);

    // 특정 유저가 누른 좋아요 목록
    List<PostLike> findByUser(UserEntity user);

    // 좋아요 단건 객체 조회
    Optional<PostLike> findByPostAndUser(Post post, UserEntity user);
}