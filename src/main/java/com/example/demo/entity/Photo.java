package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "photo")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    private Long postId;
    private Long userId;
    private String url;

    @Builder
    public Photo(Long postId, Long userId, String url) {
        this.postId = postId;
        this.userId = userId;
        this.url = url;
    }
}

