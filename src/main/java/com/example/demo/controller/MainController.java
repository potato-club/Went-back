package com.example.demo.controller;

import com.example.demo.dto.response.CategoryPostPreviewResponseDTO;
import com.example.demo.dto.response.UserHomeResponseDTO;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.MainService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Main API", description = "메인 페이지 데이터 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private final MainService mainService;
    private final UserService userService;

    @Operation(summary = "메인 페이지 게시물 조회", description = "카테고리별 최신 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<CategoryPostPreviewResponseDTO> getMainContent(@RequestParam Long categoryId) {
        CategoryPostPreviewResponseDTO categoryPostPreview = mainService.getCategoryPostPreview(categoryId);
        return ResponseEntity.ok(categoryPostPreview);
    }

    @Operation(summary = "사용자 요약 정보 조회", description = "마이페이지에서 사용자 요약 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserHomeResponseDTO> getUserSummary(@CurrentUser CustomUserDetails currentUser) {
        UserHomeResponseDTO userHomeResponseDTO = userService.getUserHomeResponseDTO(currentUser.getUserId());
        return ResponseEntity.ok(userHomeResponseDTO);
    }
}
