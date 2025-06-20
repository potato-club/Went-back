//package com.example.demo.controller;
//
//import com.example.demo.dto.CategoryDTO;
//import com.example.demo.service.CategoryService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Tag(name = "Category API", description = "카테고리 관련 API")
//@RestController
//@RequestMapping("/api/categories")
//public class CategoryController {
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
//    @PostMapping
//    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
//        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
//    }
//
//    @Operation(summary = "전체 카테고리 조회", description = "등록된 모든 카테고리를 조회합니다.")
//    @GetMapping
//    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
//        return ResponseEntity.ok(categoryService.getAllCategories());
//    }
//
//    @Operation(summary = "카테고리 단건 조회", description = "ID를 기준으로 단일 카테고리를 조회합니다.")
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
//        CategoryDTO categoryDTO = categoryService.getCategory(id);
//        return categoryDTO != null ? ResponseEntity.ok(categoryDTO) : ResponseEntity.notFound().build();
//    }
//
//    @Operation(summary = "카테고리 수정", description = "ID에 해당하는 카테고리를 수정합니다.")
//    @PutMapping("/{id}")
//    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO) {
//        categoryDTO.setCategoryId(id);
//        CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO);
//        return updatedCategory != null ? ResponseEntity.ok(updatedCategory) : ResponseEntity.notFound().build();
//    }
//
//    @Operation(summary = "카테고리 삭제", description = "ID에 해당하는 카테고리를 삭제합니다.")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.noContent().build();
//    }
//}
