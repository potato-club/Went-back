package com.example.demo.controller;

import com.example.demo.social.kakao.dto.KakaoLoginDTO;
import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.social.google.dto.GoogleLoginDTO;
import com.example.demo.social.google.GoogleOAuthService;
import com.example.demo.social.kakao.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OAuth API", description = "소셜 로그인 관련 API")
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final GoogleOAuthService googleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;

    @Operation(summary = "구글 로그인", description = "구글 로그인으로 새로운 사용자 등록 및 기존 사용자 로그인")
    @PostMapping("/google")
    public ResponseEntity<UserResponseDTO> googleLogin(@RequestBody GoogleLoginDTO googleLoginDTO, HttpServletResponse response) {
        return ResponseEntity.ok(googleOAuthService.loginWithGoogle(googleLoginDTO.getIdToken(), response));
    }

    @Operation(summary = "카카오 로그인", description = "카카오 로그인으로 새로운 사용자 등록 및 기존 사용자 로그인")
    @PostMapping("/kakao")
    public ResponseEntity<UserResponseDTO> kakaoLogin(@RequestBody KakaoLoginDTO kakaoLoginDTO, HttpServletResponse response) {
        return ResponseEntity.ok(kakaoOAuthService.loginWithKakao(kakaoLoginDTO.getCode(), response));
    }

}