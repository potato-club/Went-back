package com.example.demo.controller;

import com.example.demo.dto.PostCategoryDTO;
import com.example.demo.service.PostCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PostCategory API", description = "게시글-카테고리 연결 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post-categories")
public class PostCategoryController {
    private final PostCategoryService postCategoryService;

    @Operation(summary = "게시글-카테고리 연결 생성", description = "게시글과 카테고리를 연결합니다.")
    @PostMapping
    public ResponseEntity<PostCategoryDTO> createPostCategory(@RequestBody PostCategoryDTO postCategoryDTO) {
        return ResponseEntity.ok(postCategoryService.createPostCategory(postCategoryDTO));
    }

    @Operation(summary = "전체 연결 조회", description = "모든 게시글-카테고리 연결 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PostCategoryDTO>> getAllPostCategories() {
        return ResponseEntity.ok(postCategoryService.getAllPostCategories());
    }

    @Operation(summary = "단일 연결 조회", description = "ID로 특정 게시글-카테고리 연결 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PostCategoryDTO> getPostCategory(@PathVariable Long id) {
        PostCategoryDTO postCategoryDTO = postCategoryService.getPostCategory(id);
        return postCategoryDTO != null ? ResponseEntity.ok(postCategoryDTO) : ResponseEntity.notFound().build();
    }

//    @Operation(summary = "연결 수정", description = "ID에 해당하는 게시글-카테고리 연결 정보를 수정합니다.")
//    @PutMapping("/{id}")
//    public ResponseEntity<PostCategoryDTO> updatePostCategory(@PathVariable Long id, @RequestBody PostCategoryDTO postCategoryDTO) {
//        postCategoryDTO.setPostCategoryId(id);
//        PostCategoryDTO updatedPostCategory = postCategoryService.updatePostCategory(postCategoryDTO);
//        return updatedPostCategory != null ? ResponseEntity.ok(updatedPostCategory) : ResponseEntity.notFound().build();
//    }

    @Operation(summary = "연결 삭제", description = "ID에 해당하는 게시글-카테고리 연결을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostCategory(@PathVariable Long id) {
        postCategoryService.deletePostCategory(id);
        return ResponseEntity.noContent().build();
    }
}
