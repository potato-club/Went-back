package com.example.demo.service;

import com.example.demo.dto.response.CommentResponseDTO;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
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

    // 댓글 등록
    public CommentResponseDTO add(Long postId, Long userId, String content, Long parentId) {
        Comment saved = commentRepo.save(
                Comment.builder()
                        .postId(postId)
                        .userId(userId)
                        .content(content)
                        .parentId(parentId)
                        .build()
        );
        return toDTO(saved);
    }

    // 댓글 전체 조회 (최신순)
    public List<CommentResponseDTO> getAll(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::toDTO)
                .toList();
    }

    // 댓글 삭제 (본인 댓글만 가능)
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepo.delete(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDTO update(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(newContent);
        comment.setUpdatedAt(java.time.LocalDateTime.now()); // <-- 수동 설정 추가

        return toDTO(comment);
    }

    // 페이징 처리된 댓글 목록
    public Page<CommentResponseDTO> getPaged(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepo.findByPostId(postId, pageable)
                .map(this::toDTO);
    }

    // Comment → DTO 변환
    private CommentResponseDTO toDTO(Comment c) {
        return CommentResponseDTO.builder()
                .id(c.getId())
                .postId(c.getPostId())
                .userId(c.getUserId())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    public List<CommentResponseDTO> getStructuredComments(Long postId) {
        return commentRepo.findByPostIdOrderByParentIdAscCreatedAtAsc(postId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}