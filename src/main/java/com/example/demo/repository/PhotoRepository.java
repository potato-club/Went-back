package com.example.demo.repository;

import com.example.demo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // 게시글 ID로 모든 사진 가져오기 (다중 대응)
    List<Photo> findAllByPostId(Long postId);

    // 게시글 ID로 단일 사진 가져오기 (단건 대응)
    Optional<Photo> findFirstByPostId(Long postId);
}
