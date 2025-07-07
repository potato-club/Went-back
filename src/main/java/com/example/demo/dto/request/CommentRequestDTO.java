package com.example.demo.dto.request;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private Long postId;
    private String content;
    private Long parentId;
}