package com.example.demo.repository;

import com.example.demo.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCategory_CategoryId(Long categoryId, Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                LEFT JOIN FETCH p.likes
                WHERE p.user.userId = :userId
                ORDER BY p.createdAt DESC
            """)
    List<Post> findTop4WithLikesByUserId(@Param("userId") Long userId);

    @Query("""
                SELECT p FROM PostLike pl
                JOIN pl.post p
                LEFT JOIN FETCH p.likes
                WHERE pl.user.userId = :userId
                ORDER BY pl.createdAt DESC
            """)
    List<Post> findLikedPostWithLikesByUser(@Param("userId") Long userId, Pageable pageable);

    List<Post> findTop6ByCategory_CategoryIdOrderByCreatedAtDesc(Long categoryId);
}