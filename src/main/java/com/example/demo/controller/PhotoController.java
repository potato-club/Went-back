package com.example.demo.controller;

import com.example.demo.dto.PhotoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.PhotoService;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    @Autowired
    private PhotoService photoService;

    @PostMapping
    public ResponseEntity<PhotoDTO> createPhoto(@RequestBody PhotoDTO photoDTO) {
        PhotoDTO createdPhoto = photoService.createPhoto(photoDTO);
        return ResponseEntity.ok(createdPhoto);
    }

    @GetMapping
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        List<PhotoDTO> photos = photoService.getAllPhotos();
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDTO> getPhoto(@PathVariable Long id) {
        PhotoDTO photoDTO = photoService.getPhoto(id);
        return photoDTO != null ? ResponseEntity.ok(photoDTO) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhotoDTO> updatePhoto(@PathVariable Long id, @RequestBody PhotoDTO photoDTO) {
        photoDTO.setPhotoId(id);
        PhotoDTO updatedPhoto = photoService.updatePhoto(photoDTO);
        return updatedPhoto != null ? ResponseEntity.ok(updatedPhoto) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}
