package com.example.demo.repository;

import com.example.demo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // 특정 게시글에 속한 모든 사진 조회
    List<Photo> findAllByPostId(Long postId);

    // 여러 게시글의 모든 사진 조회 (추가)
    List<Photo> findAllByPostIdIn(List<Long> postIds);
}