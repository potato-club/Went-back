package com.example.demo.controller;

import com.example.demo.dto.PostCategoryDTO;
import com.example.demo.service.PostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-categories")
public class PostCategoryController {
    @Autowired
    private PostCategoryService postCategoryService;

    @PostMapping
    public ResponseEntity<PostCategoryDTO> createPostCategory(@RequestBody PostCategoryDTO postCategoryDTO) {
        return ResponseEntity.ok(postCategoryService.createPostCategory(postCategoryDTO));
    }

    @GetMapping
    public ResponseEntity<List<PostCategoryDTO>> getAllPostCategories() {
        return ResponseEntity.ok(postCategoryService.getAllPostCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostCategoryDTO> getPostCategory(@PathVariable Long id) {
        PostCategoryDTO postCategoryDTO = postCategoryService.getPostCategory(id);
        return postCategoryDTO != null ? ResponseEntity.ok(postCategoryDTO) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostCategoryDTO> updatePostCategory(@PathVariable Long id, @RequestBody PostCategoryDTO postCategoryDTO) {
        postCategoryDTO.setPostCategoryId(id);
        PostCategoryDTO updatedPostCategory = postCategoryService.updatePostCategory(postCategoryDTO);
        return updatedPostCategory != null ? ResponseEntity.ok(updatedPostCategory) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostCategory(@PathVariable Long id) {
        postCategoryService.deletePostCategory(id);
        return ResponseEntity.noContent().build();
    }
}


