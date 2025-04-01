package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사진 정보를 담는 DTO")
public class PhotoDTO {

    @Schema(description = "사진 ID", example = "101")
    private Long photoId;

    @Schema(description = "연결된 게시글 ID", example = "10")
    private Long postId;

    @Schema(description = "사진 URL", example = "https://example.com/images/photo.jpg")
    private String url;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
