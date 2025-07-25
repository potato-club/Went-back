package com.example.demo.controller;

import com.example.demo.jwt.JwtProvider;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "일반 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "토큰 재발급", description = "AT, RT를 재발급합니다.", security = @SecurityRequirement(name = ""))
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(HttpServletResponse response, @RequestHeader("Refresh-Token") String refreshToken) {
        authService.reissueToken(response, refreshToken);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "로그아웃", description = "AT를 무효화하고, Redis에서 RT를 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveAccessToken(request);
        authService.logout(accessToken);
        return ResponseEntity.noContent().build();
    }
}
