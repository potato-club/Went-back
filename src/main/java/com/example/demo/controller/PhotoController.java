package com.example.demo.controller;

import com.example.demo.entity.Photo;
import com.example.demo.repository.PhotoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Photo API", description = "사진 관련 API")
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;

    public PhotoController(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Operation(summary = "게시글 ID로 사진 URL 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<String> getPhotoByPostId(@PathVariable Long postId) {
        return photoRepository.findFirstByPostId(postId)
                .map(photo -> ResponseEntity.ok(photo.getUrl()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
