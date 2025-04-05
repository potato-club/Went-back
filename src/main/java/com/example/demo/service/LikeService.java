package com.example.demo.service;

import com.example.demo.entity.PostLike;
import com.example.demo.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository likeRepo;

    public boolean toggleLike(Long postId, Long userId) {
        if (likeRepo.existsByPostIdAndUserId(postId, userId)) {
            likeRepo.deleteByPostIdAndUserId(postId, userId);
            return false;
        } else {
            likeRepo.save(new PostLike(null, postId, userId));
            return true;
        }
    }

    public long countLikes(Long postId) {
        return likeRepo.countByPostId(postId);
    }
}

