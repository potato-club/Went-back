package com.example.demo.controller;

import com.example.demo.entity.Photo;
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
    public ResponseEntity<Photo> createPhoto(@RequestBody Photo photo) {
        return ResponseEntity.ok(photoService.createPhoto(photo));
    }

    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhotos() {
        return ResponseEntity.ok(photoService.getAllPhotos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable Long id) {
        return ResponseEntity.ok(photoService.getPhoto(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Photo> updatePhoto(@PathVariable Long id, @RequestBody Photo photo) {
        photo.setPhotoId(id);
        return ResponseEntity.ok(photoService.updatePhoto(photo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return ResponseEntity.noContent().build();
    }
}

