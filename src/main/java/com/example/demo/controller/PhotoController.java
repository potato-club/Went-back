package com.example.demo.controller;

import com.example.demo.dto.PhotoDTO;
import com.example.demo.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Photo API", description = "사진 관련 API")
@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Operation(summary = "사진 등록", description = "새로운 사진을 등록합니다.")
    @PostMapping
    public ResponseEntity<PhotoDTO> createPhoto(@RequestBody PhotoDTO photoDTO) {
        PhotoDTO createdPhoto = photoService.createPhoto(photoDTO);
        return ResponseEntity.ok(createdPhoto);
    }

    @Operation(summary = "전체 사진 조회", description = "등록된 모든 사진을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        List<PhotoDTO> photos = photoService.getAllPhotos();
        return ResponseEntity.ok(photos);
    }

    @Operation(summary = "단일 사진 조회", description = "ID를 기준으로 사진을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getPhoto(@PathVariable Long id) {
        PhotoDTO photoDTO = photoService.getPhoto(id);
        return photoDTO != null ? ResponseEntity.ok(photoDTO) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "사진 수정", description = "ID를 기준으로 사진 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<PhotoDTO> updatePhoto(@PathVariable Long id, @RequestBody PhotoDTO photoDTO) {
        photoDTO.setPhotoId(id);
        PhotoDTO updatedPhoto = photoService.updatePhoto(photoDTO);
        return updatedPhoto != null ? ResponseEntity.ok(updatedPhoto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "사진 삭제", description = "ID를 기준으로 사진을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
