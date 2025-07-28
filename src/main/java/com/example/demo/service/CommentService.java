package com.example.demo.service;

import com.example.demo.dto.response.CommentResponseDTO;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    // 댓글 등록
    public CommentResponseDTO add(Long postId, Long userId, String content, Long parentId) {
        // post와 user 유효성 검증
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
        Comment parent = parentId != null ? commentRepo.findById(parentId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부모 댓글입니다.")) : null;

        Comment saved = commentRepo.save(
                Comment.builder()
                        .post(post)
                        .user(user)
                        .content(content)
                        .parent(parent)
                        .build()
        );

        post.setCommentCount(post.getCommentCount() + 1);
        postRepo.save(post);

        return toDTO(saved);
    }

    // 댓글 전체 조회 (최신순)
    public List<CommentResponseDTO> getAll(Long postId) {
        return commentRepo.findByPost_PostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::toDTO)
                .toList();
    }

    // 댓글 삭제 (본인 댓글만 가능)
    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }

        Post post = comment.getPost();
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepo.save(post);

        commentRepo.delete(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDTO update(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(newContent);
        comment.setUpdatedAt(java.time.LocalDateTime.now());

        return toDTO(comment);
    }

    // 페이징 처리된 댓글 목록
    public Page<CommentResponseDTO> getPaged(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepo.findByPost_PostId(postId, pageable)
                .map(this::toDTO);
    }

    // Comment → DTO 변환
    private CommentResponseDTO toDTO(Comment c) {
        return CommentResponseDTO.builder()
                .id(c.getId())
                .postId(c.getPost() != null ? c.getPost().getPostId() : null)
                .userId(c.getUser() != null ? c.getUser().getUserId() : null)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .build();
    }

    public List<CommentResponseDTO> getStructuredComments(Long postId) {
        return commentRepo.findByPost_PostIdOrderByParentIdAscCreatedAtAsc(postId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}