package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategory_CategoryId(Long categoryId, Pageable pageable); // 수정
    List<Post> findByUser_UserId(Long userId); // 수정
    List<Post> findTop3ByCategory_CategoryIdOrderByCreatedAtDesc(Long categoryId);
}