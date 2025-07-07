package com.example.demo.service;

import com.example.demo.entity.PostLike;
import com.example.demo.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    // 좋아요 누르기
    @Transactional
    public void likePost(Long postId, Long userId) {
        boolean alreadyLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        postLikeRepository.save(new PostLike(null, postId, userId));
    }

    // 좋아요 취소
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    // 좋아요 여부 확인
    @Transactional(readOnly = true)
    public boolean hasLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // 좋아요 수 조회
    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    // 내가 좋아요 누른 게시글 목록 조회 (예: 북마크, 마이페이지)
    @Transactional(readOnly = true)
    public List<Long> getLikedPostIdsByUser(Long userId) {
        return postLikeRepository.findByUserId(userId).stream()
                .map(PostLike::getPostId)
                .toList();
    }
}