package com.example.demo.dto;

public class PhotoDTO {
    private Long photoId;
    private Long postId;
    private String url;


    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

