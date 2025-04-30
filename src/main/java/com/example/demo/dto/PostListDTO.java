package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PostListDTO {
    private Long postId;
    private String title;
    private String content;
    private String photoUrl;
    private long likes;
    private long comments;
    private double stars;
    private LocalDate createdAt;
}
