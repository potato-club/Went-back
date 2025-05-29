package com.example.demo.controller;

import com.example.demo.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Photo API", description = "사진 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageUploadController {
    private final S3Service s3Service;

    @Operation(summary = "이미지 업로드 (게시물 작성 전용)")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestPart("file") MultipartFile file) {
        String url = s3Service.uploadImageOnly(file);  // 아래 메서드 따로 만듬
        return ResponseEntity.ok(url);
    }
}
