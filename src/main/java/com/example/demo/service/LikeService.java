package com.example.demo.service;

import com.example.demo.entity.PostLike;
import com.example.demo.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository likeRepo;

    /**
     * 좋아요 토글 (좋아요 추가/취소)
     */
    public boolean toggleLike(Long postId, Long userId) {
        if (likeRepo.existsByPostIdAndUserId(postId, userId)) {
            likeRepo.deleteByPostIdAndUserId(postId, userId);
            return false; // 좋아요 취소
        } else {
            likeRepo.save(new PostLike(null, postId, userId));
            return true; // 좋아요 추가
        }
    }

    /**
     * 게시글의 총 좋아요 수 반환
     */
    public long countLikes(Long postId) {
        return likeRepo.countByPostId(postId);
    }

    /**
     * 게시글에 좋아요를 누른 userId 리스트 반환
     */
    public List<Long> getUserIdsWhoLikedPost(Long postId) {
        List<PostLike> postLikes = likeRepo.findByPostId(postId);
        return postLikes.stream()
                .map(PostLike::getUserId)
                .collect(Collectors.toList());
    }
}