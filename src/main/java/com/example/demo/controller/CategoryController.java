package com.example.demo.controller;

import com.example.demo.domain.CategoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Category API", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Operation(summary = "카테고리 목록 조회", description = "사용 가능한 모든 카테고리 목록을 조회합니다.")
    @GetMapping
    public List<CategoryResponse> getCategories() {
        return Arrays.stream(CategoryType.values())
                .map(type -> new CategoryResponse(type.name(), type.getDisplayName()))
                .collect(Collectors.toList());
    }

    // 응답 DTO (내부 static class 또는 별도 파일로 작성 가능)
    public static class CategoryResponse {
        private String code; // Enum 코드 값 (예: MOVIE)
        private String name; // 한글명 (예: 영화)

        public CategoryResponse(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public String getCode() { return code; }
        public String getName() { return name; }
    }
}