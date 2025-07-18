package com.example.demo.controller;

import com.example.demo.dto.response.MainContentResponseDTO;
import com.example.demo.security.CurrentUser;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Main API", description = "메인 페이지 데이터 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private final MainService mainService;

    @Operation(summary = "메인 페이지 조회", description = "로그인된 사용자의 정보와 카테고리별 최신 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<MainContentResponseDTO> getMainContent(@CurrentUser CustomUserDetails currentUser) {
        MainContentResponseDTO mainContentResponseDTO = mainService.getMainContent(currentUser.getUserId());
        return ResponseEntity.ok(mainContentResponseDTO);
    }
}
