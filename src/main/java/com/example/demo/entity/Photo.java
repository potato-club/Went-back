package com.example.demo.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    private Long postId;
    private Long userId;
    private String url;

    // (필요시 업로드 날짜, 파일명 등 추가)

    public Photo() {}

    public Long getPhotoId() { return photoId; }

    public void setPhotoId(Long photoId) { this.photoId = photoId; }

    public Long getPostId() { return postId; }

    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }
}