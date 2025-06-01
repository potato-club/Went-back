package com.example.demo.repository;

import com.example.demo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // 특정 게시글에 속한 모든 사진 조회
    List<Photo> findAllByPostId(Long postId);

    // 필요하다면 추가 쿼리 메서드 선언 가능
}