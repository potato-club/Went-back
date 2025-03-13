package com.example.demo.controller;

import com.example.demo.entity.PostCategory;
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
    public ResponseEntity<PostCategory> createPostCategory(@RequestBody PostCategory postCategory) {
        return ResponseEntity.ok(postCategoryService.createPostCategory(postCategory));
    }

    @GetMapping
    public ResponseEntity<List<PostCategory>> getAllPostCategories() {
        return ResponseEntity.ok(postCategoryService.getAllPostCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostCategory> getPostCategory(@PathVariable Long id) {
        return ResponseEntity.ok(postCategoryService.getPostCategory(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostCategory> updatePostCategory(@PathVariable Long id, @RequestBody PostCategory postCategory) {
        postCategory.setPostCategoryId(id);
        return ResponseEntity.ok(postCategoryService.updatePostCategory(postCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostCategory(@PathVariable Long id) {
        postCategoryService.deletePostCategory(id);
        return ResponseEntity.noContent().build();
    }
}

