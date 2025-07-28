package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.entity.PostLike;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.PostLikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 누르기
    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        boolean alreadyLiked = postLikeRepository.existsByPostAndUser(post, user);
        if (alreadyLiked) throw new IllegalStateException("이미 좋아요를 눌렀습니다.");

        postLikeRepository.save(new PostLike(post, user));

        // 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    // 좋아요 취소
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        postLikeRepository.deleteByPostAndUser(post, user);

        // 좋아요 수 감소
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }

    // 좋아요 여부 확인
    @Transactional(readOnly = true)
    public boolean hasLiked(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        return postLikeRepository.existsByPostAndUser(post, user);
    }

    // 좋아요 수 조회
    @Transactional(readOnly = true)
    public long countLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        return postLikeRepository.countByPost(post);
    }

    // 내가 좋아요 누른 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<Long> getLikedPostIdsByUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
        return postLikeRepository.findByUser(user).stream()
                .map(pl -> pl.getPost().getPostId())
                .toList();
    }
    // 게시글에 대해 내가 누른 좋아요 객체 조회 (Optional 반환)
    @Transactional(readOnly = true)
    public PostLike getMyPostLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        return postLikeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new NoSuchElementException("좋아요를 누른 이력이 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Long> getLikedUserIds(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        return postLikeRepository.findByPost(post).stream()
                .map(pl -> pl.getUser().getUserId())
                .toList();
    }
}