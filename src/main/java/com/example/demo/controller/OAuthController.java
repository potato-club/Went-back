package com.example.demo.controller;

import com.example.demo.dto.response.UserResponseDTO;
import com.example.demo.dto.request.GoogleLoginDTO;
import com.example.demo.social.google.GoogleOAuthService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {
    private final GoogleOAuthService googleOAuthService;

    @Operation(summary = "사용자 등록", description = "구글 로그인으로 새로운 사용자를 등록합니다.")
    @PostMapping("/google")
    public ResponseEntity<UserResponseDTO> googleLogin(@RequestBody GoogleLoginDTO googleLoginDTO, HttpServletResponse response) {
        return ResponseEntity.ok(googleOAuthService.loginWithGoogle(googleLoginDTO.getIdToken(), response));
    }

}